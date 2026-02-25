package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.service.ExpiredPushService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PushScheduler {

    private final ExpiredPushService expiredPushService;

    public PushScheduler(ExpiredPushService expiredPushService) {
        this.expiredPushService = expiredPushService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void deactivateStaleSubscriptions() {
        expiredPushService.deactivateStaleSubscriptions();
    }

    @Scheduled(cron = "0 * * * * *")
    public void deleteSubscriptionsInactive() {
        expiredPushService.deleteSubscriptionsInactive();
    }
}
