package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.CommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.CommentMapper;
import com.guisebastiao.lifeshotsapi.repository.CommentRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.CommentService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final PostRepository postRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PushSenderService pushSenderService;
    private final CommentMapper commentMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public CommentServiceImpl(CommentRepository commentRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, PostRepository postRepository, AuthenticatedUserProvider authenticatedUserProvider, PushSenderService pushSenderService, CommentMapper commentMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.postRepository = postRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.pushSenderService = pushSenderService;
        this.commentMapper = commentMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<CommentResponse> createComment(String postId, CommentRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.comment-service.methods.create-comment.not-found")));

        Comment comment = commentMapper.toEntity(dto);
        comment.setProfile(profile);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        if (!profile.getId().equals(post.getProfile().getId())) {
            String title = getMessage("messages.comment-post.title");
            String message = getMessage("messages.comment-post.message", new Object[]{ profile.getUser().getHandle() });
            UUID receiverId = post.getProfile().getUser().getId();

            if (notifyUser()) {
                pushSenderService.sendPush(title, message, receiverId);
            }

            Notification notification = createNotification(title, message, post.getProfile(), profile);
            notificationRepository.save(notification);
        }

        return DefaultResponse.success(commentMapper.toDTO(savedComment));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<CommentResponse>> findAllComments(String postId, PaginationParam pagination) {
        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.comment-service.methods.find-all-comments.not-found")));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt")));

        Page<Comment> resultPage = commentRepository.findAllByPost(post, pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<CommentResponse> data = resultPage.getContent().stream()
                .map(commentMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional
    public DefaultResponse<CommentResponse> updateComment(String commentId, CommentRequest dto) {
        Comment comment = findCommentAndBelongsToTheProfile(commentId);

        commentMapper.updateComment(dto, comment);

        Comment savedComment = commentRepository.save(comment);

        return DefaultResponse.success(commentMapper.toDTO(savedComment));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteComment(String commentId) {
        Comment comment = findCommentAndBelongsToTheProfile(commentId);
        comment.setDeleted(true);

        commentRepository.save(comment);

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);

        postRepository.save(post);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> removeCommentInPost(String postId, String commentId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.comment-service.methods.remove-comment-in-post.post-not-found")));

        Comment comment = commentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.comment-service.methods.remove-comment-in-post.comment-not-found")));

        if (!post.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("services.comment-service.methods.remove-comment-in-post.forbidden"));
        }

        if (comment.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.comment-service.methods.remove-comment-in-post.bad-request"));
        }

        comment.setRemoved(true);
        commentRepository.save(comment);

        return DefaultResponse.success();
    }

    private Comment findCommentAndBelongsToTheProfile(String commentId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = commentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.comment-service.methods.find-comment-and-belongs-to-the-profile.not-found")));

        if (!profile.getId().equals(comment.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("services.comment-service.methods.find-comment-and-belongs-to-the-profile.forbidden"));
        }

        return comment;
    }

    private boolean notifyUser() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyCommentPost() && setting.isNotifyAllNotifications();
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(NotificationType.COMMENT_POST);
        return notification;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
