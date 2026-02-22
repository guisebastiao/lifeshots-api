package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "push_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PushSubscription extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 1000)
    private String endpoint;

    @Column(nullable = false, length = 500)
    private String p256dh;

    @Column(nullable = false, length = 500)
    private String auth;

    @Column(name = "user_agent", nullable = false, length = 500)
    private String userAgent;

    @Column(name = "device_id", nullable = false, length = 500)
    private String deviceId;

    @Column(nullable = false)
    private boolean active = true;

    private Instant lastUsedAt;
}
