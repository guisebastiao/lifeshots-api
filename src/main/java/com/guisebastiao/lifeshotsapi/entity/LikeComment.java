package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_comments")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class LikeComment extends Auditable {

    @EmbeddedId
    private LikeCommentId id;

    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
}
