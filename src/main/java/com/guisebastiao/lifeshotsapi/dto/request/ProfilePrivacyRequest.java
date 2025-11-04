package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProfilePrivacyRequest(
        @NotNull(message = "Informe a privacidade da conta")
        boolean privacy
) { }
