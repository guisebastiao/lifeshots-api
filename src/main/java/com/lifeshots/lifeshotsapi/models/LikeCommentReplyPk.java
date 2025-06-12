package com.lifeshots.lifeshotsapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class LikeCommentReplyPk {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "comment_reply_id", nullable = false)
    private UUID commentReplyId;
}
