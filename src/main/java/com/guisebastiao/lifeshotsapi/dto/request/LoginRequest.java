package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank(message = "{validation.login-request.email.not-blank}")
        @Email(message = "{validation.login-request.email.is-valid}")
        @Length(max = 250, message = "{validation.login-request.email.length}")
        String email,

        @NotBlank(message = "{validation.login-request.password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.login-request.password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.login-request.password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.login-request.password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.login-request.password.pattern.two-numbers}"),
        })
        String password
) { }
