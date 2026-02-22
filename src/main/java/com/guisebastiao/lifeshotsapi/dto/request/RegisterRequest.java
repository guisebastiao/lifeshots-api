package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.passwordMatcher.PasswordMatcher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@PasswordMatcher(first = "password", second = "confirmPassword", message = "{validation.register-request.password-matcher.message}")
public record RegisterRequest(
        @NotBlank(message = "{validation.register-request.handle.not-blank}")
        @Length(min = 3, max = 20, message = "{validation.register-request.handle.length}")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9._]*$",
                message = "{validation.register-request.handle.pattern.handle-validation}"
        )
        String handle,

        @NotBlank(message = "{validation.register-request.fullname.not-blank}")
        @Length(min = 3, max = 250, message = "{validation.register-request.fullname.length}")
        String fullName,

        @NotBlank(message = "{validation.register-request.email.not-blank}")
        @Email(message = "{validation.register-request.email.is-valid}")
        @Length(max = 250, message = "{validation.register-request.email.length}")
        String email,

        @NotBlank(message = "{validation.register-request.password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.register-request.password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.register-request.password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.register-request.password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.register-request.password.pattern.two-numbers}"),
        })
        String password,

        @NotBlank(message = "{validation.register-request.confirm-password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.register-request.confirm-password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.register-request.confirm-password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.register-request.confirm-password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.register-request.confirm-password.pattern.two-numbers}"),
        })
        String confirmPassword
) { }
