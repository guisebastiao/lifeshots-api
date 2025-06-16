package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.ResetPassword;
import com.lifeshots.lifeshotsapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, UUID> {
    Optional<ResetPassword> findByToken(String token);

    @Modifying
    @Query("DELETE FROM ResetPassword rp WHERE rp.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM ResetPassword rp WHERE rp.expiresIn < :date")
    void deleteAllByExpiryDateBefore(@Param("date") LocalDateTime date);
}
