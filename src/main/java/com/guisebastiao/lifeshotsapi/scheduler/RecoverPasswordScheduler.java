package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.service.ExpiredRecoverPasswordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecoverPasswordScheduler {

    private final ExpiredRecoverPasswordService expiredRecoverPasswordService;

    public RecoverPasswordScheduler(ExpiredRecoverPasswordService expiredRecoverPasswordService) {
        this.expiredRecoverPasswordService = expiredRecoverPasswordService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void recoverPasswordExpired() {
        expiredRecoverPasswordService.removeRecoverExpiredPasswords();
    }
}
