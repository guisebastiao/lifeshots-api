package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.repository.DeviceRepository;
import com.guisebastiao.lifeshotsapi.repository.RefreshTokenRepository;
import com.guisebastiao.lifeshotsapi.service.ExpiredDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ExpiredDeviceServiceImpl implements ExpiredDeviceService {

    private final DeviceRepository deviceRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public ExpiredDeviceServiceImpl(DeviceRepository deviceRepository, RefreshTokenRepository refreshTokenRepository) {
        this.deviceRepository = deviceRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public void deleteDevicesExpired() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        deviceRepository.deleteDevicesWithoutRefreshTokens();
    }
}
