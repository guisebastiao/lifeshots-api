package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikePostRequest(
        @NotNull(message = "{validation.like-post-request.like.not-null}")
        boolean like
) { }
