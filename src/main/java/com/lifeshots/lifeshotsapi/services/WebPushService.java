package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.NotificationDTO;
import com.lifeshots.lifeshotsapi.dtos.request.SubscribeWebPushRequestDTO;

public interface WebPushService {
    void sendNotification(NotificationDTO notificationDTO);
    DefaultDTO subscribe(SubscribeWebPushRequestDTO subscribeWebPushRequestDTO);
}
