package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        UUID id,
        String handle,
        List<RoleResponse> roles
) {
}
