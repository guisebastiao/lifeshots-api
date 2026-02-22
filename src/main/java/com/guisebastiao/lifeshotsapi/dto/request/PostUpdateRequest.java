package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.validateFilesize.ValidateFilesize;
import com.guisebastiao.lifeshotsapi.validator.validateMaxFiles.ValidateMaxFiles;
import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record PostUpdateRequest(
        @Length(max = 300, message = "{validation.post-update-request.content.length}")
        String content,

        @ValidateMaxFiles(max = 10, message = "{validation.post-update-request.files.validate-max-files}")
        @ValidateFilesize(max = 5 * 1024 * 1024, message = "{validation.post-update-request.files.validate-filesize}")
        @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"}, message = "{validation.post-update-request.files.validate-mimetype}")
        List<MultipartFile> newFiles,

        List<UUID> removeFiles
) { }
