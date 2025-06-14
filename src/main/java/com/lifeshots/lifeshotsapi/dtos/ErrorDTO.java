package com.lifeshots.lifeshotsapi.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDTO(
        Integer status,
        String error,
        String message,
        LocalDateTime timestamp,
        List<String> details
) { }
