package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeCommentRequest;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.ConflictException;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.repository.*;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeCommentService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
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
                .orElseThrow(() -> new NotFoundException("services.like-comment-service.methods.like-comment.not-found"));

        boolean alreadyLiked = likeCommentRepository.existsByCommentAndProfile(comment, profile);

        if (alreadyLiked == dto.like()) {
            throw new ConflictException(dto.like() ? "services.like-comment-service.methods.like-comment.conflict-already-liked" : "services.like-comment-service.methods.like-comment.conflict-not-liked");
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
        LikeCommentId id = LikeCommentId.builder()
                .commentId(comment.getId())
                .profileId(profile.getId())
                .build();

        LikeComment like = LikeComment.builder()
                .id(id)
                .comment(comment)
                .profile(profile)
                .build();

        likeCommentRepository.save(like);

        if (profile.getId().equals(comment.getProfile().getId())) {
            return;
        }

        Language lang = comment.getProfile().getUser().getUserLanguage();

        String title = messageSource.getMessage("messages.like-comment.title", null, lang.getLocale());
        String message = messageSource.getMessage("messages.like-comment.message", new Object[]{ profile.getUser().getHandle() }, lang.getLocale());
        User receiver = comment.getProfile().getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, comment.getProfile(), profile);
        notificationRepository.save(notification);
    }

    private void unlikeComment(Comment comment, Profile profile) {
        LikeCommentId id = LikeCommentId.builder()
                .commentId(comment.getId())
                .profileId(profile.getId())
                .build();

        likeCommentRepository.findById(id).ifPresent(likeCommentRepository::delete);
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        return Notification.builder()
                .title(title)
                .message(message)
                .sender(sender)
                .receiver(receiver)
                .type(NotificationType.LIKE_COMMENT)
                .build();
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyLikeComment() && setting.isNotifyAllNotifications();
    }
}
