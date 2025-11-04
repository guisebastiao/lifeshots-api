package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stories")
public class Story extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 150)
    private String caption;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Column(name = "is_expired", nullable = false)
    private boolean isExpired = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @OneToOne(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private StoryPicture storyPicture;

    @OneToMany(mappedBy = "story",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeStory> likeStories;
}
