package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;

public interface PushNotificationService {
    void sendNotification(Profile sender, Profile receiver, String title, String body, NotificationType type);
}
