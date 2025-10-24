package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String handle,
        String email
) { }
