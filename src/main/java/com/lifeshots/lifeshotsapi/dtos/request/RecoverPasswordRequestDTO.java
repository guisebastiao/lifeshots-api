package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecoverPasswordRequestDTO(
        @NotBlank(message = "Informe o email")
        @Email(message = "Email inválido")
        @Size(max = 250, message = "O email tem que ser menor de 250 caracteres")
        String email
) { }
