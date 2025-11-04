package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StoryExpired {

    private static final Logger logger = LoggerFactory.getLogger(StoryExpired.class);

    @Autowired
    private StoryRepository storyRepository;

    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void storyExpired() {
        Instant now = Instant.now();
        List<Story> stories = this.storyRepository.findAllStoriesExpired(now);

        stories.stream().peek(story -> story.setExpired(true));

        this.storyRepository.saveAll(stories);

        logger.info("number of expired stories: {}", stories.size());
    }
}
