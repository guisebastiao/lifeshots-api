package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LikeStoryId implements Serializable {
    private UUID profileId;
    private UUID storyId;
}
