package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.passwordMatcher.PasswordMatcher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@PasswordMatcher(first = "newPassword", second = "confirmPassword", message = "{validation.update-password-request.password-matcher.message}")
public record UpdatePasswordRequest(

        @NotBlank(message = "{validation.update-password-request.current-password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.update-password-request.current-password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.update-password-request.current-password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.update-password-request.current-password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.update-password-request.current-password.pattern.two-numbers}"),
        })
        String currentPassword,

        @NotBlank(message = "{validation.update-password-request.new-password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.update-password-request.new-password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.update-password-request.new-password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.update-password-request.new-password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.update-password-request.new-password.pattern.two-numbers}"),
        })
        String newPassword,

        @NotBlank(message = "{validation.update-password-request.confirm-password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.update-password-request.confirm-password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.update-password-request.confirm-password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.update-password-request.confirm-password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.update-password-request.confirm-password.pattern.two-numbers}"),
        })
        String confirmPassword
) { }
