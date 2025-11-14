package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record SearchProfileRequest(
        @NotBlank(message = "Informe o nome do perfil que quer pesquisar")
        @Length(min = 3, message = "É necessario ao menos 3 caracteres para buscar um perfil")
        String search
) { }
