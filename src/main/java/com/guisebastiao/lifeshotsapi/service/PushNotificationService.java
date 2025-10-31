package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.NotificationDTO;

public interface PushNotificationService {
    void sendNotification(NotificationDTO dto);
}
