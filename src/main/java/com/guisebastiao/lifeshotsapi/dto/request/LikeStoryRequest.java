package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeStoryRequest(
        @NotNull(message = "{validation.like-story-request.like.not-null}")
        boolean like
) { }
