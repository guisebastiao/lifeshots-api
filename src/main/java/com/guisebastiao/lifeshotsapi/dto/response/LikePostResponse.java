package com.guisebastiao.lifeshotsapi.dto.response;

import java.time.Instant;

public record LikePostResponse(
        ProfileResponse profile,
        Instant createdAt
) { }
