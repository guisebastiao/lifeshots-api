package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ReplyCommentResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.ReplyCommentMapper;
import com.guisebastiao.lifeshotsapi.repository.*;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.service.ReplyCommentService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReplyCommentServiceImpl implements ReplyCommentService {

    private final ReplyCommentRepository replyCommentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PushSenderService pushSenderService;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ReplyCommentMapper replyCommentMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public ReplyCommentServiceImpl(ReplyCommentRepository replyCommentRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, CommentRepository commentRepository, PostRepository postRepository, PushSenderService pushSenderService, AuthenticatedUserProvider authenticatedUserProvider, ReplyCommentMapper replyCommentMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.replyCommentRepository = replyCommentRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.pushSenderService = pushSenderService;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.replyCommentMapper = replyCommentMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<ReplyCommentResponse> createReplyComment(String commentId, ReplyCommentRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = commentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.reply-comment-service.methods.create-reply-comment.not-found")));

        ReplyComment replyComment = replyCommentMapper.toEntity(dto);
        replyComment.setProfile(profile);
        replyComment.setComment(comment);

        ReplyComment savedReplyComment = replyCommentRepository.save(replyComment);

        comment.setReplyCommentCount(comment.getReplyCommentCount() + 1);
        commentRepository.save(comment);

        if (!profile.getId().equals(comment.getProfile().getId())) {
            String title = getMessage("messages.reply-comment.title");
            String message = getMessage("messages.reply-comment.message", new Object[]{ profile.getUser().getHandle() });
            User receiver = comment.getProfile().getUser();

            if (notifyUser(receiver)) {
                pushSenderService.sendPush(title, message, receiver.getId());
            }

            Notification notification = createNotification(title, message, comment.getProfile(), profile);
            notificationRepository.save(notification);
        }

        return DefaultResponse.success(replyCommentMapper.toDTO(savedReplyComment));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<ReplyCommentResponse>> findAllReplyComments(String commentId, PaginationParam pagination) {
        Comment comment = commentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.reply-comment-service.methods.find-all-reply-comments.not-found")));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt")));

        Page<ReplyComment> resultPage = replyCommentRepository.findAllByComment(comment, pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<ReplyCommentResponse> data = resultPage.getContent().stream()
                .map(replyCommentMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional
    public DefaultResponse<ReplyCommentResponse> updateReplyComment(String replyCommentId, ReplyCommentRequest dto) {
        ReplyComment replyComment = findReplyCommentAndBelongsToTheProfile(replyCommentId);

        replyCommentMapper.updateReplyComment(dto, replyComment);

        ReplyComment savedComment = replyCommentRepository.save(replyComment);

        return DefaultResponse.success(replyCommentMapper.toDTO(savedComment));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteReplyComment(String replyCommentId) {
        ReplyComment replyComment = findReplyCommentAndBelongsToTheProfile(replyCommentId);
        replyComment.setDeleted(true);

        replyCommentRepository.save(replyComment);

        Comment comment = replyComment.getComment();
        comment.setReplyCommentCount(comment.getReplyCommentCount() - 1);

        commentRepository.save(comment);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> removeReplyCommentInComment(String postId, String replyCommentId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.reply-comment-service.methods.remove-reply-comment-in-comment.post-not-found")));

        ReplyComment replyComment = replyCommentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(replyCommentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.reply-comment-service.methods.remove-reply-comment-in-comment.comment-not-found")));

        if (!post.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("services.reply-comment-service.methods.remove-reply-comment-in-comment.forbidden"));
        }

        if (replyComment.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.reply-comment-service.methods.remove-reply-comment-in-comment.bad-request"));
        }

        replyComment.setRemoved(true);
        replyCommentRepository.save(replyComment);

        return DefaultResponse.success();
    }

    private ReplyComment findReplyCommentAndBelongsToTheProfile(String replyCommentId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        ReplyComment replyComment = replyCommentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(replyCommentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.reply-comment-service.methods.find-reply-comment-and-belongs-to-the-profile.not-found")));

        if (!profile.getId().equals(replyComment.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("services.reply-comment-service.methods.find-reply-comment-and-belongs-to-the-profile.forbidden"));
        }

        return replyComment;
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(NotificationType.REPLY_COMMENT);
        return notification;
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyReplyComment() && setting.isNotifyAllNotifications();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
