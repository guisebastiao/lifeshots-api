package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;

public interface StoryService {
    DefaultResponse<StoryResponse> createStory(StoryRequest dto);
    DefaultResponse<StoryResponse> findStoryById(String storyId);
    DefaultResponse<StoryResponse> updateStory(String storyId, StoryRequest dto);
    DefaultResponse<Void> deleteStory(String storyId);
}
