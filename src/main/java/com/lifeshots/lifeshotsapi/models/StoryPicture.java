package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "story_pictures")
public class StoryPicture extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 45, name = "object_id", unique = true, nullable = false)
    private String objectId;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;
}
