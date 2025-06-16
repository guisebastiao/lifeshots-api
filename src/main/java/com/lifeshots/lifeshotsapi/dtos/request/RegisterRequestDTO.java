package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Informe seu nome de usuário")
        @Size(min = 3, message = "O nome de usuário tem que ser maior de 3 caracteres")
        @Size(max = 50, message = "O nome de usuário tem que ser menor de 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "O nome de usuário deve conter apenas alfanuméricos sem acentos, underline e hífen")
        String nickname,

        @NotBlank(message = "Informe seu nome")
        @Size(min = 3, message = "O nome tem que ser maior de 3 caracteres")
        @Size(max = 50, message = "O nome tem que ser menor de 50 caracteres")
        @Pattern(regexp = "^[\\p{L}]+$", message = "O nome deve conter apenas letras")
        String name,

        @NotBlank(message = "Informe seu sobrenome")
        @Size(min = 3, message = "O sobrenome tem que ser maior de 3 caracteres")
        @Size(max = 50, message = "O sobrenome tem que ser menor de 50 caracteres")
        @Pattern(regexp = "^[\\p{L}]+$", message = "O sobrenome deve conter apenas letras")
        String surname,

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
