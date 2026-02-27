package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeCommentRequest;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.repository.*;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeCommentService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LikeCommentServiceImpl implements LikeCommentService {

    private final LikeCommentRepository likeCommentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final CommentRepository commentRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PushSenderService pushSenderService;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public LikeCommentServiceImpl(LikeCommentRepository likeCommentRepository, NotificationSettingRepository notificationSettingRepository, NotificationRepository notificationRepository, CommentRepository commentRepository, AuthenticatedUserProvider authenticatedUserProvider, PushSenderService pushSenderService, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.likeCommentRepository = likeCommentRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.notificationRepository = notificationRepository;
        this.commentRepository = commentRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.pushSenderService = pushSenderService;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> likeComment(String commentId, LikeCommentRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = commentRepository.findByIdAndNotDeletedAndNotRemoved(uuidConverter.toUUID(commentId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.like-comment-service.methods.like-comment.not-found")));

        boolean alreadyLiked = likeCommentRepository.existsByCommentAndProfile(comment, profile);

        if (alreadyLiked == dto.like()) {
            throw new BusinessException(BusinessHttpStatus.CONFLICT, dto.like() ?
                    getMessage("services.like-comment-service.methods.like-comment.conflict-already-liked") :
                    getMessage("services.like-comment-service.methods.like-comment.conflict-not-liked")
            );
        }

        if (dto.like()) {
            likeComment(comment, profile);
            comment.setLikeCount(comment.getLikeCount() + 1);
        } else {
            unlikeComment(comment, profile);
            comment.setLikeCount(comment.getLikeCount() - 1);
        }

        commentRepository.save(comment);

        return DefaultResponse.success();
    }

    private void likeComment(Comment comment, Profile profile) {
        LikeCommentId id = new LikeCommentId(profile.getId(), comment.getId());

        LikeComment like = new LikeComment();
        like.setId(id);
        like.setComment(comment);
        like.setProfile(profile);

        likeCommentRepository.save(like);

        if (profile.getId().equals(comment.getProfile().getId())) {
            return;
        }

        String title = getMessage("messages.like-comment.title");
        String message = getMessage("messages.like-comment.message", new Object[]{ profile.getUser().getHandle() });
        User receiver = comment.getProfile().getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, comment.getProfile(), profile);
        notificationRepository.save(notification);
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(NotificationType.LIKE_COMMENT);
        return notification;
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyLikeComment() && setting.isNotifyAllNotifications();
    }

    private void unlikeComment(Comment comment, Profile profile) {
        LikeCommentId id = new LikeCommentId(profile.getId(), comment.getId());
        likeCommentRepository.findById(id).ifPresent(likeCommentRepository::delete);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
