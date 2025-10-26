package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ForgotPasswordRequest(
        @NotBlank(message = "Informe seu email")
        @Email(message = "Informe um email válido")
        @Length(max = 250, message = "O email tem que possuir no máximo 250 caracteres")
        String email
) { }
