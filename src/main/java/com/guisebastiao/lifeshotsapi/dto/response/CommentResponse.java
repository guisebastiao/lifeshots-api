package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        int likeCount,
        int replyCommentCount,
        boolean isOwner,
        boolean isLiked,
        ProfileResponse profile
) { }
