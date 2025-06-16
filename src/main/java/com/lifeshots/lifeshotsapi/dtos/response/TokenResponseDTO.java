package com.lifeshots.lifeshotsapi.dtos.response;

import java.time.Instant;

public record TokenResponseDTO(
        String token,
        Instant expires
) { }
