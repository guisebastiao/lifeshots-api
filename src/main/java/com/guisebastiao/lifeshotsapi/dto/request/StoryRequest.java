package com.guisebastiao.lifeshotsapi.dto.request;


import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import com.guisebastiao.lifeshotsapi.validator.validateFileSize.ValidateFileSize;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record StoryRequest(
        @Length(max = 150, message = "A descrição do story tem que possuir no máximo 150 caracteres")
        String caption,

        @NotNull(message = "A imagem do story é obrigatória")
        @ValidateFileSize(max = 5 * 1024 * 1024, message = "A imagem do story deve ter no máximo 5MB")
        @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"})
        MultipartFile file
) { }
