package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.repository.LikeReplyCommentRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.ReplyCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeReplyCommentService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LikeReplyCommentServiceImpl implements LikeReplyCommentService {

    private final LikeReplyCommentRepository likeReplyCommentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final ReplyCommentRepository replyCommentRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PushSenderService pushSenderService;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public LikeReplyCommentServiceImpl(LikeReplyCommentRepository likeReplyCommentRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, ReplyCommentRepository replyCommentRepository, AuthenticatedUserProvider authenticatedUserProvider, PushSenderService pushSenderService, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.likeReplyCommentRepository = likeReplyCommentRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.replyCommentRepository = replyCommentRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.pushSenderService = pushSenderService;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> likeReplyComment(String replyCommentId, LikeReplyCommentRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        ReplyComment replyComment = replyCommentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(replyCommentId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.like-reply-comment-service.methods.like-reply-comment.not-found")));

        boolean alreadyLiked = likeReplyCommentRepository.existsByReplyCommentAndProfile(replyComment, profile);

        if (alreadyLiked == dto.like()) {
            throw new BusinessException(BusinessHttpStatus.CONFLICT, dto.like() ?
                    getMessage("services.like-reply-comment-service.methods.like-reply-comment.conflict-already-liked") :
                    getMessage("services.like-reply-comment-service.methods.like-reply-comment.conflict-not-liked")
            );
        }

        if (dto.like()) {
            likeComment(replyComment, profile);
            replyComment.setLikeCount(replyComment.getLikeCount() + 1);
        } else {
            unlikeComment(replyComment, profile);
            replyComment.setLikeCount(replyComment.getLikeCount() - 1);
        }

        replyCommentRepository.save(replyComment);

        return DefaultResponse.success();
    }

    private void likeComment(ReplyComment replyComment, Profile profile) {
        LikeReplyCommentId id = new LikeReplyCommentId(profile.getId(), replyComment.getId());

        LikeReplyComment like = new LikeReplyComment();
        like.setId(id);
        like.setProfile(profile);
        like.setReplyComment(replyComment);

        likeReplyCommentRepository.save(like);

        if (profile.getId().equals(replyComment.getProfile().getId())) {
            return;
        }

        String title = getMessage("messages.like-reply-comment.title");
        String message = getMessage("messages.like-reply-comment.message", new Object[]{ profile.getUser().getHandle() });
        User receiver = replyComment.getProfile().getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, replyComment.getProfile(), profile);
        notificationRepository.save(notification);
    }

    private void unlikeComment(ReplyComment replyComment, Profile profile) {
        LikeReplyCommentId id = new LikeReplyCommentId(profile.getId(), replyComment.getId());
        likeReplyCommentRepository.findById(id).ifPresent(likeReplyCommentRepository::delete);
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(NotificationType.LIKE_REPLY_COMMENT);
        return notification;
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyLikeReplyComment() && setting.isNotifyAllNotifications();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
