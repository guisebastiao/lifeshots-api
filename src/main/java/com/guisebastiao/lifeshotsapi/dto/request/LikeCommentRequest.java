package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeCommentRequest(
        @NotNull
        boolean like
) { }
