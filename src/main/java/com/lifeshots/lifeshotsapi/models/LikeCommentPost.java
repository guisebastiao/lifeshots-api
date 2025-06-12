package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "likes_comment_posts")
public class LikeCommentPost extends Auditable {

    @EmbeddedId
    private LikeCommentPostPk id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("commentPostId")
    @JoinColumn(name = "comment_post_id")
    private CommentPost commentPost;
}
