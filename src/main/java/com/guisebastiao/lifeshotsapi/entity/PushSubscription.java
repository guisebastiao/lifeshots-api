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
@Table(name = "push_subscriptions")
public class PushSubscription extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 1000, unique = true)
    private String endpoint;

    @Column(nullable = false, length = 500)
    private String p256dh;

    @Column(nullable = false, length = 500)
    private String auth;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
