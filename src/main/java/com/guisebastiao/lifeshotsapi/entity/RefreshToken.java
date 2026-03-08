package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RefreshToken extends Auditable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "refresh_token", nullable = false, unique = true, updatable = false)
    private UUID refreshToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;
}
