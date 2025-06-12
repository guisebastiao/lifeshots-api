package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "profile_pictures")
public class ProfilePicture extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 256, name = "object_id", nullable = false, unique = true)
    private String objectId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
