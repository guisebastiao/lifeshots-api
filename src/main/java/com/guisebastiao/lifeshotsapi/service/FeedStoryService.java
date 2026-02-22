package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.StoryFeedResponse;

import java.util.List;

public interface FeedStoryService {
    DefaultResponse<List<StoryFeedResponse>> feed(PaginationParam pagination);
}
