package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "settings")
public class Setting extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Boolean notificationsDisabled = false;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
