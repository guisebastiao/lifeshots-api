package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Envie o refresh token")
        String refreshToken,

        @NotBlank(message = "Envie o access token")
        String accessToken
) { }
