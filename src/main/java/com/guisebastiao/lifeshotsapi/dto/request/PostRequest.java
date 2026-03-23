package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.validateFilesize.ValidateFilesize;
import com.guisebastiao.lifeshotsapi.validator.validateMaxFiles.ValidateMaxFiles;
import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostRequest(
        @Length(max = 300)
        String content,

        @NotNull(message = "{validation.post-request.files.not-null}")
        @ValidateMaxFiles(max = 10)
        @ValidateFilesize(max = 5 * 1024 * 1024)
        @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"})
        List<MultipartFile> files
) { }
