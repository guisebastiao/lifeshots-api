package com.lifeshots.lifeshotsapi.dtos;

import java.util.List;

public record ErrorDTO(
        Integer status,
        String name,
        String path,
        List<FieldErrorDTO> fieldErrors
) { }
