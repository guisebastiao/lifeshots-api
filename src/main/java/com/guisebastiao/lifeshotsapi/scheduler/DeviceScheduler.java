package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.service.ExpiredDeviceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeviceScheduler {

    private final ExpiredDeviceService expiredDeviceService;

    public DeviceScheduler(ExpiredDeviceService expiredDeviceService) {
        this.expiredDeviceService = expiredDeviceService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void deleteSubscriptionsInactive() {
        expiredDeviceService.deleteDevicesExpired();
    }
}
