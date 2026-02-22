package com.guisebastiao.lifeshotsapi.dto.params;

import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordTokenParam(
        @NotBlank(message = "{validation.recover-password-token-param.token.not-blank}")
        String token
) {}
