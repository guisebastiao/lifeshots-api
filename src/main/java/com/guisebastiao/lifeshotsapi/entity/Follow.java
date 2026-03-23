package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "follows")
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
