package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ForgotPasswordRequest(
        @NotBlank
        @Email
        @Length(max = 250)
        String email
) { }
