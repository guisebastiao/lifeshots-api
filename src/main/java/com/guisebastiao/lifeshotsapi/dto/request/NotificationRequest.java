package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record NotificationRequest(
        @NotEmpty(message = "A lista de notificações não pode estar vazia")
        List<UUID> ids
) { }
