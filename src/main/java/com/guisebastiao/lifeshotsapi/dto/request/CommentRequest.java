package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CommentRequest(
        @NotBlank(message = "{validation.comment-request.content.not-blank}")
        @Length(max = 300, message = "{validation.comment-request.content.length}")
        String content
) { }
