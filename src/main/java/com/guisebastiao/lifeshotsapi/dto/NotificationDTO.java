package com.guisebastiao.lifeshotsapi.dto;

public record NotificationDTO(
        String endpoint,
        String p256dh,
        String auth,
        NotificationPayload payload
) { }
