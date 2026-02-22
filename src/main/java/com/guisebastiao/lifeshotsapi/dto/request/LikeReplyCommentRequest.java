package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeReplyCommentRequest(
        @NotNull(message = "{validation.like-reply-comment-request.like.not-null}")
        boolean like
) { }
