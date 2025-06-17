package com.lifeshots.lifeshotsapi.dtos.request;

import com.lifeshots.lifeshotsapi.validations.FileContentType.FileContentType;
import com.lifeshots.lifeshotsapi.validations.FileSize.FileSize;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProfilePictureRequestDTO(
        @NotNull(message = "A imagem é obrigatória")
        @FileSize(max = 5 * 1024 * 1024, message = "A imagem de perfil deve ter no máximo 5MB")
        @FileContentType(allowed = {"image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic", "image/heif"}, message = "Extenção da imagem não é permitida")
        MultipartFile file
) { }
