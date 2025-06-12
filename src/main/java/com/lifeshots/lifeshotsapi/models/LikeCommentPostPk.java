package com.lifeshots.lifeshotsapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class LikeCommentPostPk {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "comment_post_id", nullable = false)
    private UUID commentPostId;
}
