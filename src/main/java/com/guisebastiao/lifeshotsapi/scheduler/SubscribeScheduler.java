package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.service.ExpiredSubscribeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SubscribeScheduler {

    private final ExpiredSubscribeService expiredSubscribeService;

    public SubscribeScheduler(ExpiredSubscribeService expiredSubscribeService) {
        this.expiredSubscribeService = expiredSubscribeService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void deleteSubscriptionsInactive() {
        expiredSubscribeService.deleteSubscribesExpired();
    }
}
