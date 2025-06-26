package com.lifeshots.lifeshotsapi.dtos.response;

import com.lifeshots.lifeshotsapi.models.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryResponseDTO(
    UUID id,
    String content,
    Integer amountLikes,
    UserResponseDTO user,
    LocalDateTime createdAt,
    String storyPicture
) { }
