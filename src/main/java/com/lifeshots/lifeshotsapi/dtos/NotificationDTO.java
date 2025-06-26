package com.lifeshots.lifeshotsapi.dtos;

import com.lifeshots.lifeshotsapi.dtos.response.UserResponseDTO;
import com.lifeshots.lifeshotsapi.enums.NotificationType;
import nl.martijndwars.webpush.Subscription;

public record NotificationDTO(
        UserResponseDTO sender,
        UserResponseDTO receiver,
        NotificationType type,
        String title,
        String message,
        Subscription subscription
) { }
