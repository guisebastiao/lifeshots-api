package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(
        @NotNull(message = "{validation.follow-request.follow.not-null}")
        boolean follow
) { }
