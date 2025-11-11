package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, UUID> {
}
