package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.read = false AND n.isDeleted = false AND n.receiver.id = :userId")
    long countAllNotificationsUnreadByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.receiver.id = :userId AND n.read = false AND n.isDeleted = false")
    void updateAllToReadByUserId(@Param("userId") UUID userId);

    @Query("SELECT n FROM Notification n WHERE n.id IN :notificationsId AND n.receiver.id = :userId AND n.isDeleted = false")
    List<Notification> findAllNotificationsByIdsAndUserId(@Param("notificationsId") List<UUID> notificationsId, @Param("userId") UUID userId);

    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.receiver.id = :userId
          AND (:read IS NULL OR n.read = :read)
          AND n.isDeleted = false
        ORDER BY
            CASE WHEN n.read = false THEN 0 ELSE 1 END,
            n.createdAt DESC
    """)
    Page<Notification> findAllNotificationsByUserId(@Param("userId") UUID userId, @Param("read") Boolean read, Pageable pageable);
}
