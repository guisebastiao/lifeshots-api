package com.guisebastiao.lifeshotsapi.dto.params;

import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordTokenParam(
        @NotBlank
        String token
) {}
