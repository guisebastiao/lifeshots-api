package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SearchProfileRequest(
        @NotBlank(message = "{validation.search-profile-request.search.not-blank}")
        @Length(min = 3, message = "{validation.search-profile-request.search.length}")
        String search
) { }
