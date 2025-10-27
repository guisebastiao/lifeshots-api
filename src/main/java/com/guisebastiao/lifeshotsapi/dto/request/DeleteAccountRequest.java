package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record DeleteAccountRequest(
        @NotBlank(message = "Informe sua senha")
        @Length(min = 6, max = 20, message = "Sua senha deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "Sua senha deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "Sua senha deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "Sua senha deve conter pelo menos dois números"),
        })
        String password
) {}
