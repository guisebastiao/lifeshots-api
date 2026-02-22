package com.guisebastiao.lifeshotsapi.dto.response;

import com.guisebastiao.lifeshotsapi.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String message,
        NotificationType type,
        boolean read,
        Instant createdAt
) { }
