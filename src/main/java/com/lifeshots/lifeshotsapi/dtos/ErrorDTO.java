package com.lifeshots.lifeshotsapi.dtos;

import java.util.List;

public record ErrorDTO(
        String code,
        String messageError,
        String path,
        List<FieldErrorDTO> fieldErrors
) { }
