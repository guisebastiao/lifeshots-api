package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank(message = "Informe seu email")
        @Email(message = "Informe um email válido")
        @Length(max = 250, message = "O email tem que possuir no máximo 250 caracteres")
        String email,

        @NotBlank(message = "Informe sua senha")
        @Length(min = 6, max = 20, message = "Sua senha deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "A senha deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "A senha deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "A senha deve conter pelo menos dois números"),
        })
        String password
) { }
