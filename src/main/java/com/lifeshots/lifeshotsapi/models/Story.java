package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "stories")
public class Story extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 150, nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer amountLikes = 0;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private List<LikeStory> likeStories;

    @OneToMany(mappedBy = "story")
    private List<StoryPicture> storyPictures;
}
