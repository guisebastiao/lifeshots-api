package com.lifeshots.lifeshotsapi.dtos.response;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        LocalDateTime expiresIn,
        String token
){ }
