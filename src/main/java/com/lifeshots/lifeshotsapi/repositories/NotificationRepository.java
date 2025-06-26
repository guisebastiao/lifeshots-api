package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.Notification;
import com.lifeshots.lifeshotsapi.models.NotificationPk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, NotificationPk> {
    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :userId")
    Page<Notification> findAllNotificationBelongsAuthenticatedUser(@Param("userId") UUID userId, Pageable pageable);
}
