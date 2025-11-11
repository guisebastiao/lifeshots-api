package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reply_comments")
public class ReplyComment extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 300)
    private String content;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "is_removed", nullable = false)
    private boolean isRemoved = false;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @OneToMany(mappedBy = "replyComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeReplyComment> likeReplyComments;
}
