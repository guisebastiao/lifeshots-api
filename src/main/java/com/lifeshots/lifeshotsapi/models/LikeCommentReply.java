package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "likes_comment_replies")
public class LikeCommentReply extends Auditable {

    @EmbeddedId
    private LikeCommentReplyPk id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("commentReplyId")
    @JoinColumn(name = "comment_reply_id")
    private CommentReply commentReply;
}
