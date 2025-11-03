package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.ValidateMimetype.ValidateMimetype;
import com.guisebastiao.lifeshotsapi.validator.validateFileSize.ValidateFileSize;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProfilePictureRequest(
    @NotNull(message = "A foto de perfil é obrigatória")
    @ValidateFileSize(max = 5 * 1024 * 1024, message = "A foto de perfil deve ter no máximo 5MB")
    @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"})
    MultipartFile file
){ }
