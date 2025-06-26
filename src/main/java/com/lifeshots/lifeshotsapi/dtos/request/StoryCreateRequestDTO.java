package com.lifeshots.lifeshotsapi.dtos.request;

import com.lifeshots.lifeshotsapi.validations.FileContentType.FileContentType;
import com.lifeshots.lifeshotsapi.validations.FileSize.FileSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record StoryCreateRequestDTO(
        @NotBlank(message = "A descrição do story é obrigatória")
        @Size(max = 150, message = "A descrição tem que ser menor de 150 caracteres")
        String content,

        @NotNull(message = "A imagem do story é obrigatória")
        @FileSize(max = 5 * 1024 * 1024, message = "A imagem de story deve ter no máximo 5MB")
        @FileContentType(allowed = {"image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic", "image/heif"}, message = "Extenção da imagem não é permitida")
        MultipartFile file
) { }
