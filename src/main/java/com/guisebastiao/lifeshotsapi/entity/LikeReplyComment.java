package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_reply_comments")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class LikeReplyComment extends Auditable {

    @EmbeddedId
    private LikeReplyCommentId id;

    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne
    @MapsId("replyCommentId")
    @JoinColumn(name = "reply_comment_id", nullable = false)
    private ReplyComment replyComment;

}
