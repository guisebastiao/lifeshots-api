package com.lifeshots.lifeshotsapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class FollowPk implements Serializable {

    @Column(name = "following_id", nullable = false)
    private UUID followingId;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;
}
