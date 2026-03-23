package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.ConflictException;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.LikePostRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikePostService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikePostServiceImpl implements LikePostService {

    private final LikePostRepository likePostRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final PostRepository postRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PushSenderService pushSenderService;
    private final ProfileMapper profileMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public LikePostServiceImpl(LikePostRepository likePostRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, PostRepository postRepository, AuthenticatedUserProvider authenticatedUserProvider, PushSenderService pushSenderService, ProfileMapper profileMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.likePostRepository = likePostRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.postRepository = postRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.pushSenderService = pushSenderService;
        this.profileMapper = profileMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> likePost(String postId, LikePostRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new NotFoundException("services.like-post-service.methods.like-post.not-found"));

        boolean alreadyLiked = likePostRepository.alreadyLikedPost(post, profile);

        if (alreadyLiked == dto.like()) {
            throw new ConflictException(dto.like() ? "services.like-post-service.methods.like-post.conflict-already-liked" : "services.like-post-service.methods.like-post.conflict-not-liked");
        }

        if (dto.like()) {
            likePost(post, profile);
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            unlikePost(post, profile);
            post.setLikeCount(post.getLikeCount() - 1);
        }

        postRepository.save(post);

        return DefaultResponse.success();
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<ProfileResponse>> findAllLikePost(String postId, PaginationParam pagination) {
        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new NotFoundException("services.like-post-service.methods.find-all-like-post.not-found"));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Profile> resultPage = likePostRepository.findAllByPost(post, pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<ProfileResponse> data = resultPage.getContent().stream()
                .map(profileMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    private void likePost(Post post, Profile profile) {
        LikePostId id = LikePostId.builder()
                .postId(post.getId())
                .profileId(profile.getId())
                .build();

        LikePost like = LikePost.builder()
                .id(id)
                .post(post)
                .profile(profile)
                .build();

        likePostRepository.save(like);

        if (profile.getId().equals(post.getProfile().getId())) {
            return;
        }

        Language lang = post.getProfile().getUser().getUserLanguage();

        String title = messageSource.getMessage("messages.like-post.title", null, lang.getLocale());
        String message = messageSource.getMessage("messages.like-post.message", new Object[]{ profile.getUser().getHandle() }, lang.getLocale());
        User receiver = post.getProfile().getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, post.getProfile(), profile);
        notificationRepository.save(notification);
    }

    private void unlikePost(Post post, Profile profile) {
        LikePostId id = LikePostId.builder()
                .postId(post.getId())
                .profileId(profile.getId())
                .build();

        likePostRepository.findById(id).ifPresent(likePostRepository::delete);
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        return Notification.builder()
                .title(title)
                .message(message)
                .sender(sender)
                .receiver(receiver)
                .type(NotificationType.LIKE_POST)
                .build();
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyLikePost() && setting.isNotifyAllNotifications();
    }
}
