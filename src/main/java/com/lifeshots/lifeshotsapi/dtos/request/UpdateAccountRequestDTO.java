package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequestDTO(
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


        @Size(max = 150, message = "A biografia tem que ser menor de 150 caracteres")
        String bio
) { }
