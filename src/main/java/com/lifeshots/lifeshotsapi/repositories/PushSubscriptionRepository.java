package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.PushSubscription;
import com.lifeshots.lifeshotsapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {
    @Query("SELECT p FROM PushSubscription p WHERE p.user.id = :userId")
    Optional<PushSubscription> findByUserId(@Param("userId") UUID userId);
}
