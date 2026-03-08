package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.device.user.id = :userId")
    List<PushSubscription> findAllByDeviceUser(@Param("userId") UUID userId);
}
