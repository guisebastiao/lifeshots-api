package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikeStoryResponse;

public interface LikeStoryService {
    DefaultResponse<Void> likeStory(String storyId, LikeStoryRequest dto);
    DefaultResponse<PageResponse<LikeStoryResponse>> findAllLikeStory(String storyId, PaginationFilter pagination);
}
