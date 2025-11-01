package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Notification;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findAllByReceiver(Profile receiver, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :receiver AND n.read = FALSE")
    List<Notification> findAllByNotificationsAndNotRead(@Param("receiver") Profile receiver);

    @Query("SELECT n FROM Notification n WHERE n.id IN :ids")
    List<Notification> findAllNotificationsByIds(@Param("ids") List<UUID> ids);
}
