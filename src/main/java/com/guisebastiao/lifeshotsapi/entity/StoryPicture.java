package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "story_pictures")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class StoryPicture extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
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
