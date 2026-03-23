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
@Table(name = "post_pictures")
public class PostPicture extends Auditable {

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "file_key", length = 255, nullable = false, unique = true)
    private String fileKey;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "mime_type", length = 30, nullable = false)
    private String mimeType;
}
