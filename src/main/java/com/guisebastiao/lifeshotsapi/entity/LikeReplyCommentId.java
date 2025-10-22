package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class LikeReplyCommentId {
    private UUID profileId;
    private UUID replyCommentId;
}
