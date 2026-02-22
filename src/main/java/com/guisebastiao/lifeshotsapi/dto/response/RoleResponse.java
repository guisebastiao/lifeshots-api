package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.UUID;

public record RoleResponse(
        UUID id,
        String roleName
) { }
