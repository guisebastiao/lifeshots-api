package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.validateFilesize.ValidateFilesize;
import com.guisebastiao.lifeshotsapi.validator.validateMaxFiles.ValidateMaxFiles;
import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostRequest(
        @Length(max = 300, message = "{validation.post-request.content.length}")
        String content,

        @NotNull(message = "{validation.post-request.files.not-null}")
        @ValidateMaxFiles(max = 10, message = "{validation.post-request.files.validate-max-files}")
        @ValidateFilesize(max = 5 * 1024 * 1024, message = "{validation.post-request.files.validate-filesize}")
        @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"}, message = "{validation.post-request.files.validate-mimetype}")
        List<MultipartFile> files
) { }
