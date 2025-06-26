package com.lifeshots.lifeshotsapi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoryUpdateRequestDTO(
        @NotBlank(message = "A descrição do story é obrigatória")
        @Size(max = 150, message = "A descrição tem que ser menor de 150 caracteres")
        String content
) { }
