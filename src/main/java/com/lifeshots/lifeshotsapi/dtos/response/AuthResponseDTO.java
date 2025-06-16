package com.lifeshots.lifeshotsapi.dtos.response;

import java.time.Instant;

public record AuthResponseDTO(
        String token,
        Instant expires,
        UserResponseDTO user
) { }
