package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PushSubscriptionRequest(
        @NotBlank(message = "Informe o token Expo")
        String token
) { }
