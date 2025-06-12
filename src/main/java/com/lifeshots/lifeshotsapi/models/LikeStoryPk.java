package com.lifeshots.lifeshotsapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class LikeStoryPk {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "story_id", nullable = false)
    private UUID storyId;
}
