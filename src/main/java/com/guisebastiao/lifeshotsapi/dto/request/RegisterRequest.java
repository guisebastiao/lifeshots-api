package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.passwordMatcher.PasswordMatcher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatcher(first = "password", second = "confirmPassword")
public record RegisterRequest(
        @NotBlank
        @Length(min = 3, max = 20)
        String handle,

        @NotBlank
        @Length(min = 3, max = 250)
        String fullName,

        @NotBlank
        @Email
        @Length(max = 250)
        String email,

        @NotBlank
        @Length(min = 6, max = 20)
        String password,

        @NotBlank
        @Length(min = 6, max = 20)
        String confirmPassword
) { }
