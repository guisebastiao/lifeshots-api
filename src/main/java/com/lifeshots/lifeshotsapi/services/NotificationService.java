package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;

public interface NotificationService {
    DefaultDTO findAllNotifications(int offset, int limit);
}
