package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProfilePrivacyRequest(
        @NotNull
        boolean privacy
) { }
