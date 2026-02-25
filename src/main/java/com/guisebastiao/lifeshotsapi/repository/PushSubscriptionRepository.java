package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

    Optional<PushSubscription> findByIdAndUser(UUID id, User user);

    Optional<PushSubscription> findByEndpointAndUser(String endpoint, User user);

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.active = true AND ps.user.id = :receiverId")
    List<PushSubscription> findActiveByUser(@Param("receiverId") UUID receiverId);

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.active = false")
    List<PushSubscription> findAllPushSubscriptionByInactive();

    @Modifying
    @Query("UPDATE PushSubscription p SET p.active = false WHERE p.active = true AND (p.lastUsedAt IS NULL OR p.lastUsedAt < :cutoff)")
    void deactivateStaleSubscriptions(@Param("cutoff") Instant cutoff);

    @Modifying
    @Query("UPDATE PushSubscription ps SET ps.lastUsedAt = :now WHERE ps.id = :subId")
    void updateLastUsed(@Param("subId") UUID subId, @Param("now") Instant now);
}
