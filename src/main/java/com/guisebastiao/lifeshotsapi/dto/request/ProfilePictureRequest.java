package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import com.guisebastiao.lifeshotsapi.validator.validateFilesize.ValidateFilesize;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProfilePictureRequest(
    @NotNull
    @ValidateFilesize(max = 5 * 1024 * 1024)
    @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"})
    MultipartFile file
){ }
