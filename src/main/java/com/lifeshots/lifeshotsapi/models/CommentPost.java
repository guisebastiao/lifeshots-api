package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "comment_posts")
public class CommentPost extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 300, nullable = false)
    private String content;

    @Column(name = "amount_likes", nullable = false)
    private Integer amountLikes = 0;

    @Column(name = "amount_comment_reply", nullable = false)
    private Integer amountCommentReply = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "commentPost", cascade = CascadeType.ALL)
    private List<LikeCommentPost> likeCommentPosts;

    @OneToMany(mappedBy = "commentPost", cascade = CascadeType.ALL)
    private List<CommentReply> commentReplies;
}
