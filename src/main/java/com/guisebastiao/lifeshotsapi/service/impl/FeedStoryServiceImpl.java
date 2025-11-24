package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.dto.response.StoryFeedResponse;
import com.guisebastiao.lifeshotsapi.dto.response.StoryItemResponse;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.mapper.StoryMapper;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FeedStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedStoryServiceImpl implements FeedStoryService {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private StoryMapper storyMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public DefaultResponse<PageResponse<StoryFeedResponse>> feed(PaginationFilter pagination) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        List<Story> ownerStories = this.storyRepository.findAllStoriesByProfile(profile);

        StoryFeedResponse ownerFeed = new StoryFeedResponse(
                this.profileMapper.toDTO(profile),
                ownerStories.stream()
                        .map(storyMapper::toDTO)
                        .map(storyMapper::toItemDTO)
                        .toList()
        );

        Page<Story> resultPage = this.storyRepository.findAllStoriesFromFriends(profile, pageable);

        long totalGroups = this.storyRepository.countDistinctProfilesFromFriends(profile) + 1;
        long totalPages = (long) Math.ceil((double) totalGroups / pagination.limit());

        Paging paging = new Paging(totalGroups, totalPages, pagination.offset(), pagination.limit());

        List<StoryResponse> storyResponses = resultPage.getContent().stream()
                .map(storyMapper::toDTO)
                .toList();

        Map<UUID, List<StoryResponse>> grouped = storyResponses.stream()
                .collect(Collectors.groupingBy(s -> s.profile().id()));

        List<StoryFeedResponse> friendsFeed = grouped.values().stream()
                .map(stories -> {
                    ProfileResponse profileResponse = stories.getFirst().profile();
                    List<StoryItemResponse> storyItems = stories.stream()
                            .map(storyMapper::toItemDTO)
                            .toList();
                    return new StoryFeedResponse(profileResponse, storyItems);
                })
                .toList();

        List<StoryFeedResponse> finalFeed = new ArrayList<>();
        finalFeed.add(ownerFeed);
        finalFeed.addAll(friendsFeed);

        PageResponse<StoryFeedResponse> data = new PageResponse<>(finalFeed, paging);

        return new DefaultResponse<>(true, "Feed de stories retornado com sucesso", data);
    }
}
