package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Device extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private PushSubscription pushSubscription;

    @OneToOne(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;
}
