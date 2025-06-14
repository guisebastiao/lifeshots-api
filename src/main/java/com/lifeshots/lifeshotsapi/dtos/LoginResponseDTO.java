package com.lifeshots.lifeshotsapi.dtos;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        LocalDateTime expiresIn,
        String token
){ }
