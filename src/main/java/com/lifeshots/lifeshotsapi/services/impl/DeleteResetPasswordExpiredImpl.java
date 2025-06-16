package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.repositories.ResetPasswordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class DeleteResetPasswordExpiredImpl {

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60)
    public void deleteResetPasswordExpired() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        resetPasswordRepository.deleteAllByExpiryDateBefore(now);
    }
}