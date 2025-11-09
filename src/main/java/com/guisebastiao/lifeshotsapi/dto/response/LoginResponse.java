package com.guisebastiao.lifeshotsapi.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) { }
