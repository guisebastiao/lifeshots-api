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
@Table(name = "profile_pictures")
public class ProfilePicture extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "file_key", nullable = false, unique = true)
    private String fileKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "mime_type", length = 30, nullable = false)
    private String mimeType;
}
