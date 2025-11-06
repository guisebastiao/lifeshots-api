package com.guisebastiao.lifeshotsapi.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        String content,
        int likeCount,
        int commentCount,
        int shareCount,
        boolean isOwner,
        boolean isLiked,
        Instant createdAt,
        ProfileResponse profile,
        List<PostPictureResponse> postPictures
) { }
