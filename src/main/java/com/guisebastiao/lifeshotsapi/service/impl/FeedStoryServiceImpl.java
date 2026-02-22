package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.dto.response.StoryFeedResponse;
import com.guisebastiao.lifeshotsapi.dto.response.StoryItemResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.mapper.StoryMapper;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FeedStoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedStoryServiceImpl implements FeedStoryService {

    private final StoryRepository storyRepository;
    private final ProfileRepository profileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final StoryMapper storyMapper;
    private final ProfileMapper profileMapper;

    public FeedStoryServiceImpl(StoryRepository storyRepository, ProfileRepository profileRepository, AuthenticatedUserProvider authenticatedUserProvider, StoryMapper storyMapper, ProfileMapper profileMapper) {
        this.storyRepository = storyRepository;
        this.profileRepository = profileRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.storyMapper = storyMapper;
        this.profileMapper = profileMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<StoryFeedResponse>> feed(PaginationParam pagination) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<UUID> profilePage = profileRepository.findProfileIdsWithStories(profile.getId(), pageable);

        List<Story> stories = storyRepository.findAllByProfileIds(profilePage.getContent());

        Map<UUID, List<Story>> grouped = stories.stream()
                .collect(Collectors.groupingBy(s -> s.getProfile().getId()));

        List<StoryFeedResponse> data = profilePage.getContent().stream()
                .map(profileId -> {
                    List<Story> profileStories = grouped.getOrDefault(profileId, List.of());

                    ProfileResponse profileDTO = profileMapper.toDTO(profileStories.getFirst().getProfile());

                    List<StoryItemResponse> items = profileStories.stream()
                            .map(storyMapper::toDTO)
                            .map(storyMapper::toItemDTO)
                            .toList();

                    return new StoryFeedResponse(profileDTO, items);
                }).toList();

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(profilePage.getTotalElements())
                .totalPages(profilePage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        return DefaultResponse.success(data, meta);
    }
}
