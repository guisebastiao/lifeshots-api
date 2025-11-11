package com.guisebastiao.lifeshotsapi.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReplyCommentResponse(
        UUID id,
        String content,
        int likeCount,
        boolean isOwner,
        boolean isLiked,
        Instant createdAt,
        ProfileResponse profile
) { }
