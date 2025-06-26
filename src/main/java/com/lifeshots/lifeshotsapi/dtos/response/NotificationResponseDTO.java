package com.lifeshots.lifeshotsapi.dtos.response;

import com.lifeshots.lifeshotsapi.enums.NotificationType;
import com.lifeshots.lifeshotsapi.models.NotificationPk;

public record NotificationResponseDTO(
        NotificationPk id,
        UserResponseDTO sender,
        NotificationType type,
        String title,
        String message,
        Boolean isRead
) { }
