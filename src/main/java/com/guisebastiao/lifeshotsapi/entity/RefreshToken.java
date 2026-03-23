package com.guisebastiao.lifeshotsapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "refresh_token", nullable = false, unique = true, updatable = false)
    private UUID refreshToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
