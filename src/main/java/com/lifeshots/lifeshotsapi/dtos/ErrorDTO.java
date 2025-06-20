package com.lifeshots.lifeshotsapi.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDTO(
        Integer status,
        String name,
        String path,
        List<FieldErrorDTO> fieldErrors,
        LocalDateTime timestamp
        ) {
    public ErrorDTO(Integer status, String name, String path, List<FieldErrorDTO> fieldErrors) {
        this(status, name, path, fieldErrors, LocalDateTime.now());
    }
}
