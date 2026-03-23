package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record DeleteAccountRequest(
        @NotBlank
        @Length(min = 6, max = 20)
        String password
) {}
