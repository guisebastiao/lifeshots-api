package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.FollowParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.FollowRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.FollowType;
import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.BadRequestException;
import com.guisebastiao.lifeshotsapi.exception.ConflictException;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.FollowRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FollowService;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
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
    private final ProfileMapper profileMapper;
    private final PushSenderService pushSenderService;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public FollowServiceImpl(FollowRepository followRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, AuthenticatedUserProvider authenticatedUserProvider, ProfileRepository profileRepository, ProfileMapper profileMapper, PushSenderService pushSenderService, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.followRepository = followRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.pushSenderService = pushSenderService;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> follow(String profileId, FollowRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId))
                    .orElseThrow(() -> new NotFoundException("services.follow-service.methods.follow.not-found"));

        if (user.getProfile().getId().equals(profile.getId())) {
            throw new ConflictException("services.follow-service.methods.follow.conflict");
        }

        if (!dto.follow()) {
            Follow follow = followRepository.findByFollowingAndFollower(profile, user.getProfile())
                    .orElseThrow(() -> new BadRequestException("services.follow-service.methods.follow.bad-request-dont-follow"));

            followRepository.delete(follow);

            profile.setFollowersCount(profile.getFollowersCount() - 1);
            user.getProfile().setFollowingCount(user.getProfile().getFollowingCount() - 1);

            profileRepository.saveAll(List.of(profile, user.getProfile()));

            return DefaultResponse.success();
        }

        if (followRepository.existsByFollowerAndFollowing(user.getProfile(), profile)) {
            throw new ConflictException("services.follow-service.methods.follow.bad-request-already-follow");
        }

        FollowId followId = FollowId.builder()
                .followingId(profile.getId())
                .followerId(user.getProfile().getId())
                .build();

        Follow follow = Follow.builder()
                .id(followId)
                .follower(user.getProfile())
                .following(profile)
                .build();

        followRepository.save(follow);

        profile.setFollowersCount(profile.getFollowersCount() + 1);
        user.getProfile().setFollowingCount(user.getProfile().getFollowingCount() + 1);

        profileRepository.saveAll(List.of(profile, user.getProfile()));

        Language lang = profile.getUser().getUserLanguage();

        String title = messageSource.getMessage("messages.follow-account.title", null, lang.getLocale());
        String message = messageSource.getMessage("messages.follow-account.message", new Object[]{ user.getHandle() }, lang.getLocale());
        User receiver = profile.getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, profile, user.getProfile());

        notificationRepository.save(notification);

        return DefaultResponse.success();
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<ProfileResponse>> findAllFollowers(String profileId, FollowParam param, PaginationParam pagination) {
        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId))
                .orElseThrow(() -> new NotFoundException("services.follow-service.methods.find-all-followers.not-found"));

        FollowType type = FollowType.valueOf(param.type().toUpperCase());

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Profile> resultPage = findFollowsByType(type, profile, pageable);

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

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        return Notification.builder()
                .title(title)
                .message(message)
                .sender(sender)
                .receiver(receiver)
                .type(NotificationType.NEW_FOLLOWER)
                .build();
    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyNewFollower() && setting.isNotifyAllNotifications();
    }

    private Page<Profile> findFollowsByType(FollowType type, Profile profile, Pageable pageable) {
        return switch (type) {
            case FOLLOWERS -> followRepository.findByFollowing(profile, pageable);
            case FOLLOWING -> followRepository.findByFollower(profile, pageable);
        };
    }
}
