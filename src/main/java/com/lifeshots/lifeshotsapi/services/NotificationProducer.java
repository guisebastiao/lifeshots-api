package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.NotificationDTO;

public interface NotificationProducer {
    void sendNotification(NotificationDTO notificationDTO);
}
