package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowId implements Serializable {
    private UUID followerId;
    private UUID followingId;
}
