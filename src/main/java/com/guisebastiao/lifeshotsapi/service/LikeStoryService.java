package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

import java.util.List;

public interface LikeStoryService {
    DefaultResponse<Void> likeStory(String storyId, LikeStoryRequest dto);
    DefaultResponse<List<ProfileResponse>> findAllLikeStory(String storyId, PaginationParam pagination);
}
