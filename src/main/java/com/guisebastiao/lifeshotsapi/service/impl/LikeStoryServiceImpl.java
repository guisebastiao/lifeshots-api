package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.*;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.exception.*;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.LikeStoryRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeStoryService;
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
public class LikeStoryServiceImpl implements LikeStoryService {

    private final LikeStoryRepository likeStoryRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final StoryRepository storyRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PushSenderService pushSenderService;
    private final ProfileMapper profileMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public LikeStoryServiceImpl(LikeStoryRepository likeStoryRepository, NotificationRepository notificationRepository, NotificationSettingRepository notificationSettingRepository, StoryRepository storyRepository, AuthenticatedUserProvider authenticatedUserProvider, PushSenderService pushSenderService, ProfileMapper profileMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.likeStoryRepository = likeStoryRepository;
        this.notificationRepository = notificationRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.storyRepository = storyRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.pushSenderService = pushSenderService;
        this.profileMapper = profileMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> likeStory(String storyId, LikeStoryRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Story story = storyRepository.findByIdAndNotDeleted(uuidConverter.toUUID(storyId))
                .orElseThrow(() -> new NotFoundException("services.like-story-service.methods.like-story.not-found"));

        boolean alreadyLiked = likeStoryRepository.alreadyLikedStory(story, profile);

        if (story.getProfile().getId().equals(profile.getId())) {
            throw new BadRequestException("services.like-story-service.methods.like-story.bad-request");
        }

        if (alreadyLiked == dto.like()) {
            throw new ConflictException(dto.like() ? "services.like-story-service.methods.like-story.conflict-already-liked" : "services.like-story-service.methods.like-story.conflict-not-liked");
        }
        if (dto.like()) {
            likeStory(story, profile);
            story.setLikeCount(story.getLikeCount() + 1);
        } else {
            unlikeStory(story, profile);
            story.setLikeCount(story.getLikeCount() - 1);
        }

        storyRepository.save(story);

        return DefaultResponse.success();
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<ProfileResponse>> findAllLikeStory(String storyId, PaginationParam pagination) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Story story = storyRepository.findByIdAndNotDeleted(uuidConverter.toUUID(storyId))
                .orElseThrow(() -> new NotFoundException("services.like-story-service.methods.find-all-like-story.not-found"));

        if (!story.getProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException("services.like-story-service.methods.find-all-like-story.forbidden");
        }

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Profile> resultPage = likeStoryRepository.findAllByStory(story, pageable);

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

    private void likeStory(Story story, Profile profile) {
        LikeStoryId id = LikeStoryId.builder()
                .storyId(story.getId())
                .profileId(profile.getId())
                .build();

        LikeStory like = LikeStory.builder()
                .id(id)
                .story(story)
                .profile(profile)
                .build();

        likeStoryRepository.save(like);

        Language lang = story.getProfile().getUser().getUserLanguage();

        String title = messageSource.getMessage("messages.like-story.title", null, lang.getLocale());
        String message = messageSource.getMessage("messages.like-story.message", new Object[]{ profile.getUser().getHandle() }, lang.getLocale());
        User receiver = story.getProfile().getUser();

        if (notifyUser(receiver)) {
            pushSenderService.sendPush(title, message, receiver.getId());
        }

        Notification notification = createNotification(title, message, story.getProfile(), profile);
        notificationRepository.save(notification);
    }

    private void unlikeStory(Story story, Profile profile) {
        LikeStoryId id = LikeStoryId.builder()
                .storyId(story.getId())
                .profileId(profile.getId())
                .build();

        likeStoryRepository.findById(id).ifPresent(likeStoryRepository::delete);
    }

    private Notification createNotification(String title, String message, Profile receiver, Profile sender) {
        return Notification.builder()
                .title(title)
                .message(message)
                .sender(sender)
                .receiver(receiver)
                .type(NotificationType.LIKE_STORY)
                .build();

    }

    private boolean notifyUser(User user) {
        NotificationSetting setting = notificationSettingRepository.findByUser(user);
        return setting.isNotifyLikeStory() && setting.isNotifyAllNotifications();
    }
}
