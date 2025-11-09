package com.guisebastiao.lifeshotsapi.dto;

import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;

public record NotificationDTO(
        String token,
        NotificationResponse payload
) { }
