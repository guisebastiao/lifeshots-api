package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follows")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Follow extends Auditable {

    @EmbeddedId
    private FollowId id;

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name = "follower_id", nullable = false)
    private Profile follower;

    @ManyToOne
    @MapsId("followingId")
    @JoinColumn(name = "following_id", nullable = false)
    private Profile following;
}
