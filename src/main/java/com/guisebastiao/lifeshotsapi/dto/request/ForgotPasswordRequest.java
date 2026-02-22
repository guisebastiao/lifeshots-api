package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ForgotPasswordRequest(
        @NotBlank(message = "{validation.forgot-password-request.email.not-blank}")
        @Email(message = "{validation.forgot-password-request.email.is-valid}")
        @Length(max = 250, message = "{validation.forgot-password-request.email.length}")
        String email
) { }
