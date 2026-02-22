package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.LikePostMapper;
import com.guisebastiao.lifeshotsapi.repository.LikePostRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikePostService;
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

@Service
public class LikePostServiceImpl implements LikePostService {

    private final LikePostRepository likePostRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final PostRepository postRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PushSenderService pushSenderService;
    private final LikePostMapper likePostMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public LikePostServiceImpl(LikePostRepository likePostRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, PostRepository postRepository, AuthenticatedUserProvider authenticatedUserProvider, PushSenderService pushSenderService, LikePostMapper likePostMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.likePostRepository = likePostRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.postRepository = postRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.pushSenderService = pushSenderService;
        this.likePostMapper = likePostMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> likePost(String postId, LikePostRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.like-post-service.methods.like-post.not-found")));

        boolean alreadyLiked = likePostRepository.alreadyLikedPost(post, profile);

        if (alreadyLiked == dto.like()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.like() ?
                    getMessage("services.like-post-service.methods.like-post.conflict-already-liked") :
                    getMessage("services.like-post-service.methods.like-post.conflict-not-liked")
            );
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
    public DefaultResponse<List<LikePostResponse>> findAllLikePost(String postId, PaginationParam pagination) {
        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.like-post-service.methods.find-all-like-post.not-found")));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<LikePost> resultPage = likePostRepository.findAllByPost(post, pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<LikePostResponse> data = resultPage.getContent().stream()
                .map(likePostMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    private void likePost(Post post, Profile profile) {
        LikePostId id = new LikePostId(profile.getId(), post.getId());

        LikePost like = new LikePost();
        like.setId(id);
        like.setProfile(profile);
        like.setPost(post);

        likePostRepository.save(like);

        if (profile.getId().equals(post.getProfile().getId())) {
            return;
        }

        String title = getMessage("messages.like-post.title");
        String message = getMessage("messages.like-post.message", new Object[]{ profile.getUser().getHandle() });
        User receiver = post.getProfile().getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, post.getProfile(), profile);
        notificationRepository.save(notification);
    }

    private void unlikePost(Post post, Profile profile) {
        LikePostId id = new LikePostId(profile.getId(), post.getId());
        likePostRepository.findById(id).ifPresent(likePostRepository::delete);
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(NotificationType.LIKE_POST);
        return notification;
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyLikePost() && setting.isNotifyAllNotifications();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
