package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank
        @Email
        @Length(max = 250)
        String email,

        @NotBlank
        @Length(min = 6, max = 20)
        String password
) { }
