package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.RefreshToken;
import com.guisebastiao.lifeshotsapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.refreshToken = :refreshToken")
    Optional<RefreshToken> findByRefreshToken(@Param("refreshToken") UUID refreshToken);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.refreshToken = :refreshToken")
    void deleteByRefreshToken(@Param("refreshToken") UUID refreshToken);
}
