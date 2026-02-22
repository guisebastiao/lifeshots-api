package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikeStoryResponse;

import java.util.List;

public interface LikeStoryService {
    DefaultResponse<Void> likeStory(String storyId, LikeStoryRequest dto);
    DefaultResponse<List<LikeStoryResponse>> findAllLikeStory(String storyId, PaginationParam pagination);
}
