package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ReplyCommentRequest(
        @NotBlank
        @Length(max = 300)
        String content
) { }
