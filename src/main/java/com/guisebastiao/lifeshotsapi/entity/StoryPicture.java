package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "story_pictures")
public class StoryPicture extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private Story story;

    @Column(name = "file_key", nullable = false, unique = true)
    private String fileKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "mime_type", length = 30, nullable = false)
    private String mimeType;
}
