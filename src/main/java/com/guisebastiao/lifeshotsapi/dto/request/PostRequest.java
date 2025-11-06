package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.validateFileSize.ValidateFileSize;
import com.guisebastiao.lifeshotsapi.validator.validateMaxFiles.ValidateMaxFiles;
import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostRequest(
        @Length(max = 300, message = "A descrição da publicação tem que ser menor que 300 caracteres")
        String content,

        @NotNull(message = "Pelo menos uma imagem para publicação é obrigatória")
        @ValidateMaxFiles(max = 10, message = "São permitidas apenas dez imagens por publicação")
        @ValidateFileSize(max = 5 * 1024 * 1024, message = "A imagem do story deve ter no máximo 5MB")
        @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"})
        List<MultipartFile> files
) { }
