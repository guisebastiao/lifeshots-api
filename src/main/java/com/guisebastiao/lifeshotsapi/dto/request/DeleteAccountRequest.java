package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record DeleteAccountRequest(
        @NotBlank(message = "{validation.delete-account-request.password.not-blank}")
        @Length(min = 6, max = 20, message = "{validation.delete-account-request.password.length}")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "{validation.delete-account-request.password.pattern.capital-letter}"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "{validation.delete-account-request.password.pattern.special-character}"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "{validation.delete-account-request.password.pattern.two-numbers}"),
        })
        String password
) {}
