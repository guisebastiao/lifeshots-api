package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.FollowParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.enums.FollowType;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.mapper.FollowMapper;
import com.guisebastiao.lifeshotsapi.repository.FollowRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FollowService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ProfileRepository profileRepository;
    private final FollowMapper followMapper;
    private final PushSenderService pushSenderService;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public FollowServiceImpl(FollowRepository followRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, AuthenticatedUserProvider authenticatedUserProvider, ProfileRepository profileRepository, FollowMapper followMapper, PushSenderService pushSenderService, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.followRepository = followRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profileRepository = profileRepository;
        this.followMapper = followMapper;
        this.pushSenderService = pushSenderService;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> follow(String profileId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile following = profileRepository.findById(uuidConverter.toUUID(profileId))
                    .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.follow-service.methods.follow.not-found)")));

        if (user.getProfile().getId().equals(following.getId())) {
            throw new BusinessException(BusinessHttpStatus.CONFLICT, getMessage("services.follow-service.methods.follow.conflict"));
        }

        if (followRepository.existsByFollowerAndFollowing(user.getProfile(), following)) {
            throw new BusinessException(BusinessHttpStatus.CONFLICT, getMessage("services.follow-service.methods.follow.bad-request"));
        }

        FollowId followId = new FollowId();
        followId.setFollowingId(following.getId());
        followId.setFollowerId(user.getProfile().getId());

        Follow follow = new Follow();
        follow.setId(followId);
        follow.setFollower(user.getProfile());
        follow.setFollowing(following);

        followRepository.save(follow);

        following.setFollowersCount(following.getFollowersCount() + 1);
        user.getProfile().setFollowingCount(user.getProfile().getFollowingCount() + 1);

        profileRepository.saveAll(List.of(following, user.getProfile()));

        String title = getMessage("messages.follow-account.title");
        String message = getMessage("messages.follow-account.message", new Object[]{ user.getHandle() });
        User receiver = following.getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, following, user.getProfile());

        notificationRepository.save(notification);

        return DefaultResponse.success();
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<FollowResponse>> findAllMyFollowers(FollowParam param, PaginationParam pagination) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        FollowType type = typeEnum(param.type());

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Follow> resultPage = findFollowsByType(type, user.getProfile(), pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<FollowResponse> data = resultPage.getContent().stream()
                .map(follow -> followMapper.toDTO(follow, FollowType.valueOf(param.type().toUpperCase())))
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<FollowResponse>> findAllFollowers(String profileId, FollowParam param, PaginationParam pagination) {
        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.follow-service.methods.find-all-followers.not-found")));

        FollowType type = typeEnum(param.type());

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Follow> resultPage = findFollowsByType(type, profile, pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<FollowResponse> data = resultPage.getContent().stream()
                .map(follow -> followMapper.toDTO(follow, FollowType.valueOf(param.type().toUpperCase())))
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> unfollow(String profileId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.follow-service.methods.unfollow.not-found")));

        if (user.getProfile().getId().equals(profile.getId())) {
            throw new BusinessException(BusinessHttpStatus.CONFLICT, getMessage("services.follow-service.methods.unfollow.conflict"));
        }

        Follow follow = followRepository.findByFollowingAndFollower(profile, user.getProfile())
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.BAD_REQUEST, getMessage("services.follow-service.methods.unfollow.bad-request")));

        followRepository.delete(follow);

        profile.setFollowersCount(profile.getFollowersCount() - 1);
        user.getProfile().setFollowingCount(user.getProfile().getFollowingCount() - 1);

        profileRepository.saveAll(List.of(profile, user.getProfile()));

        return DefaultResponse.success();
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(NotificationType.NEW_FOLLOWER);
        return notification;
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyNewFollower() && setting.isNotifyAllNotifications();
    }

    private Page<Follow> findFollowsByType(FollowType type, Profile profile, Pageable pageable) {
        return switch (type) {
            case FOLLOWERS -> followRepository.findByFollowing(profile, pageable);
            case FOLLOWING -> followRepository.findByFollower(profile, pageable);
        };
    }

    private FollowType typeEnum(String type) {
        return FollowType.valueOf(type.toUpperCase());
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
