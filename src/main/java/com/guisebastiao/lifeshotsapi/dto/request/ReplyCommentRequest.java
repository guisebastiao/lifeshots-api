package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ReplyCommentRequest(
        @NotBlank(message = "{validation.reply-comment-request.content.not-blank}")
        @Length(max = 300, message = "{validation.reply-comment-request.content.length}")
        String content
) { }
