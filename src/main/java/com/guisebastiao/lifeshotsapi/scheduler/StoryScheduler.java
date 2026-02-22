package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.service.ExpiredStoryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StoryScheduler {

    private final ExpiredStoryService expiredStoryService;

    public StoryScheduler(ExpiredStoryService expiredStoryService) {
        this.expiredStoryService = expiredStoryService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void storyExpired() {
        expiredStoryService.removeStoryExpired();
    }
}
