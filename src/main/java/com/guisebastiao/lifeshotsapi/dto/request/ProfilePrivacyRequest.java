package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProfilePrivacyRequest(
        @NotNull(message = "{validation.profile-privacy-request.privacy.not-null}")
        boolean privacy
) { }
