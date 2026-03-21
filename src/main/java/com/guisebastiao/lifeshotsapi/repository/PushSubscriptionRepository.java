package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Device;
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

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.device = :device")
    Optional<PushSubscription> findByDevice(@Param("device") Device device);

    @Query("SELECT ps FROM PushSubscription ps WHERE ps.device.user.id = :userId")
    List<PushSubscription> findAllByDeviceUser(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM PushSubscription ps WHERE ps.active = false")
    void deleteAllByNotActive();

    @Modifying
    @Query(value = """
        INSERT INTO push_subscriptions
        (device_id, endpoint, p256dh, auth, active)
        VALUES
        (:#{#sub.device.id}, :#{#sub.endpoint}, :#{#sub.p256dh}, :#{#sub.auth}, :#{#sub.active})
        ON CONFLICT (endpoint)
        DO UPDATE SET
            device_id = EXCLUDED.device_id,
            p256dh = EXCLUDED.p256dh,
            auth = EXCLUDED.auth,
            active = true,
            updated_at = now()
        """, nativeQuery = true)
    void upsert(@Param("sub") PushSubscription sub);
}
