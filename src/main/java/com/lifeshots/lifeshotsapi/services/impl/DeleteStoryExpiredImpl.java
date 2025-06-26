package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.models.Story;
import com.lifeshots.lifeshotsapi.repositories.StoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeleteStoryExpiredImpl {

    @Autowired
    private StoryRepository storyRepository;

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60)
    public void deleteStoryExpired() {
        LocalDateTime now = LocalDateTime.now().minusHours(24);
        List<Story> stories = this.storyRepository.findAllStoriesOlderThan24Hours(now);
        storyRepository.deleteAll(stories);
    }
}
