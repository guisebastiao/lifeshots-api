package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, UUID> {

    @Query("SELECT p FROM ProfilePicture p WHERE p.user.id = :userId")
    Optional<ProfilePicture> findByUserId(@Param("userId") UUID userId);
}
