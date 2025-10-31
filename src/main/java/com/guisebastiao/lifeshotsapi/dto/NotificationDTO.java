package com.guisebastiao.lifeshotsapi.dto;

import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;

public record NotificationDTO(
        String endpoint,
        String p256dh,
        String auth,
        NotificationResponse payload
) { }
