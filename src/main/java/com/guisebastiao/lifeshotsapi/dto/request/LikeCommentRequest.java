package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeCommentRequest(
        @NotNull(message = "{validation.like-comment-request.like.not-null}")
        boolean like
) { }
