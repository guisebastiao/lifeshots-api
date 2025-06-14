package com.lifeshots.lifeshotsapi.dtos;

import java.time.Instant;

public record TokenResponseDTO(
        String token,
        Instant expires
) { }
