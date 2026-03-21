package com.guisebastiao.lifeshotsapi.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        int likeCount,
        int replyCommentCount,
        boolean isOwner,
        boolean isLiked,
        Instant createdAt,
        ProfileResponse profile
) { }
