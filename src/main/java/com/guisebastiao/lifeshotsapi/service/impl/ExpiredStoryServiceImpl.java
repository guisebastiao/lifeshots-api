package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.service.ExpiredStoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ExpiredStoryServiceImpl implements ExpiredStoryService {

    private final StoryRepository storyRepository;

    public ExpiredStoryServiceImpl(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    @Transactional
    public void removeStoryExpired() {
        Instant now = Instant.now();
        List<Story> stories = this.storyRepository.findAllStoriesExpired(now);

        if (stories.isEmpty()) return;

        stories.forEach(story -> story.setExpired(true));

        storyRepository.saveAll(stories);
    }
}
