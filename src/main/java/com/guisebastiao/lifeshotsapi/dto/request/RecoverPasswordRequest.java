package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.passwordMatcher.PasswordMatcher;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatcher(first = "newPassword", second = "confirmPassword")
public record RecoverPasswordRequest(
        @NotBlank
        @Length(min = 6, max = 20)
        String newPassword,

        @NotBlank
        @Length(min = 6, max = 20)
        String confirmPassword
) { }
