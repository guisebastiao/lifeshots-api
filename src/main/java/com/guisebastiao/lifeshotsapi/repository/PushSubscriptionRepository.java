package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.endpoint = :endpoint")
    Optional<PushSubscription> findByEndpoint(@Param("endpoint") String endpoint);

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.user.id = :userId AND ps.active = true")
    List<PushSubscription> findAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM PushSubscription ps WHERE ps.active = false")
    void deleteAllByNotActive();
}
