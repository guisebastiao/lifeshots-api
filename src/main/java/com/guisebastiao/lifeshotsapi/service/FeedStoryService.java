package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.StoryFeedResponse;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;

import java.util.List;

public interface FeedStoryService {
    DefaultResponse<PageResponse<StoryFeedResponse>> feed(PaginationFilter pagination);
    DefaultResponse<List<StoryResponse>> findMyStories();
}
