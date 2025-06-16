package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Informe o email")
        @Email(message = "Email inválido")
        @Size(max = 250, message = "O email tem que ser menor de 250 caracteres")
        String email,

        @NotBlank(message = "Informe sua senha")
        @Size(min = 6, message = "A senha deve ter mais de 6 caracteres")
        @Size(max = 20, message = "A senha deve ter menos de 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "A senha deve ter uma letra maiúscula"),
                @Pattern(regexp = ".*\\d.*\\d.*", message = "A senha deve ter dois números"),
                @Pattern(regexp = ".*[@$!%*?&.#].*", message = "A senha deve ter um caractere especial")
        })
        String password
) { }
