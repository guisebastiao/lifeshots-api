package com.guisebastiao.lifeshotsapi.dto.response;

import java.time.Instant;
import java.util.UUID;

public record StoryItemResponse(
    UUID id,
    String caption,
    int likeCount,
    boolean isExpired,
    boolean isLiked,
    boolean isOwner,
    Instant expiresAt,
    Instant createdAt,
    StoryPictureResponse storyPicture
) { }
