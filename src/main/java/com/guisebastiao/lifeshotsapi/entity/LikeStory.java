package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_stories")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class LikeStory extends Auditable{

    @EmbeddedId
    private LikeStoryId id;

    @ManyToOne
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne
    @MapsId("storyId")
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;
}
