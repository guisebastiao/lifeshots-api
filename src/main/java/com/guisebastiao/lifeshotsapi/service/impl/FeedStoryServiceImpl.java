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
import com.guisebastiao.lifeshotsapi.mapper.StoryMapper;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FeedStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public DefaultResponse<PageResponse<StoryFeedResponse>> feed(PaginationFilter pagination) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Story> resultPage = this.storyRepository.findAllStoriesFromFriends(profile, pageable);
        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<StoryResponse> storyResponses = resultPage.getContent().stream()
                .map(this.storyMapper::toDTO)
                .toList();

        Map<UUID, List<StoryResponse>> grouped = storyResponses.stream()
                .collect(Collectors.groupingBy(s -> s.profile().id()));

        List<StoryFeedResponse> feedGroups = grouped.values().stream()
                .map(stories -> {
                    ProfileResponse profileResponse = stories.getFirst().profile();
                    List<StoryItemResponse> storyItems = stories.stream().map(story -> this.storyMapper.toItemDTO(story)).toList();
                    return new StoryFeedResponse(profileResponse, storyItems);
                })
                .toList();

        PageResponse<StoryFeedResponse> data = new PageResponse<StoryFeedResponse>(feedGroups, paging);

        return new DefaultResponse<PageResponse<StoryFeedResponse>>(true, "Feed de stories retornado com sucesso", data);
    }

    @Override
    public DefaultResponse<List<StoryResponse>> findMyStories() {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        List<Story> stories = this.storyRepository.findAllStoriesByProfile(profile);

        List<StoryResponse> data = stories.stream().map(storyMapper::toDTO).toList();

        return new DefaultResponse<List<StoryResponse>>(true, "Stories retornado com sucesso", data);
    }
}
