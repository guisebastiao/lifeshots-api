package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.StoryCreateRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.StoryUpdateRequestDTO;

public interface StoryService {
    DefaultDTO createStory(StoryCreateRequestDTO storyCreateRequestDTO);
    DefaultDTO findStoryById(String storyId);
    DefaultDTO findAllStoriesBelongsFollowers(int offset, int limit);
    DefaultDTO updateStory(String storyId, StoryUpdateRequestDTO storyUpdateRequestDTO);
    DefaultDTO deleteStory(String storyId);
}
