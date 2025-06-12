package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "post_pictures")
public class PostPicture extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 45, name = "object_id", nullable = false, unique = true)
    private String objectId;
}
