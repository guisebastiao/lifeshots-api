package com.guisebastiao.lifeshotsapi.dto.response;

import java.time.Instant;

public record LikeStoryResponse(
        ProfileResponse profile,
        Instant createdAt
) { }
