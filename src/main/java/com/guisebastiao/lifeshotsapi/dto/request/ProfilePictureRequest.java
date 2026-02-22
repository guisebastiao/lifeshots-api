package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import com.guisebastiao.lifeshotsapi.validator.validateFilesize.ValidateFilesize;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ProfilePictureRequest(
    @NotNull(message = "{validation.profile-picture-request.file.not-null}")
    @ValidateFilesize(max = 5 * 1024 * 1024, message = "{validation.profile-picture-request.file.validate-filesize}")
    @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"}, message = "{validation.profile-picture-request.file.validate-mimetype}")
    MultipartFile file
){ }
