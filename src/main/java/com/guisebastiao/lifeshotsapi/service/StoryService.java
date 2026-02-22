package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.request.StoryUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;

import java.util.List;

public interface StoryService {
    DefaultResponse<StoryResponse> createStory(StoryRequest dto);
    DefaultResponse<StoryResponse> findStoryById(String storyId);
    DefaultResponse<List<StoryResponse>> findStoriesByAuthUser();
    DefaultResponse<StoryResponse> updateStory(String storyId, StoryUpdateRequest dto);
    DefaultResponse<Void> deleteStory(String storyId);
}
