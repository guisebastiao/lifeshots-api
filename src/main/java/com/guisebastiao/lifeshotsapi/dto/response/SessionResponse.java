package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.List;
import java.util.UUID;

public record SessionResponse(
        boolean isAuthenticated,
        User user
) {
    public record User(
            UUID id,
            String handle,
            List<String> roles
    ) {}
}
