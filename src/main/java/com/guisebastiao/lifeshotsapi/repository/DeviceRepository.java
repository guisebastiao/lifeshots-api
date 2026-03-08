package com.guisebastiao.lifeshotsapi.repository;

import com.guisebastiao.lifeshotsapi.entity.Device;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Device d WHERE d.id = :id")
    Optional<Device> findByIdForUpdate(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Device d WHERE d.refreshToken IS NULL")
    void deleteDevicesWithoutRefreshTokens();
}
