package com.guisebastiao.lifeshotsapi.dto.request;


import com.guisebastiao.lifeshotsapi.validator.validateMimetype.ValidateMimetype;
import com.guisebastiao.lifeshotsapi.validator.validateFilesize.ValidateFilesize;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record StoryRequest(
        @Length(max = 150, message = "{validation.story-request.caption.length}")
        String caption,

        @NotNull(message = "{validation.story-request.file.not-null}")
        @ValidateFilesize(max = 5 * 1024 * 1024, message = "{validation.story-request.file.validate-filesize}")
        @ValidateMimetype(allowed = {"image/jpeg", "image/png", "image/heic", "image/heif", "image/webp"}, message = "{validation.story-request.file.validate-mimetype}")
        MultipartFile file
) { }
