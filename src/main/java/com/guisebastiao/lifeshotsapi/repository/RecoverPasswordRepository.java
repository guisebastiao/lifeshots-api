package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.RecoverPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecoverPasswordRepository extends JpaRepository<RecoverPassword, UUID> {

    Optional<RecoverPassword> findRecoverPasswordByToken(String token);

    @Query("SELECT rp FROM RecoverPassword rp WHERE rp.expiresAt < :expiresAt AND rp.isActive = true")
    List<RecoverPassword> findRecoverPasswordByExpired(@Param("expiresAt") LocalDateTime expiresAt);
}
