package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.service.ExpiredPushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ExpiredPushServiceImpl implements ExpiredPushService {

    private final PushSubscriptionRepository pushSubscriptionRepository;

    public ExpiredPushServiceImpl(PushSubscriptionRepository pushSubscriptionRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
    }

    @Override
    @Transactional
    public void deactivateStaleSubscriptions() {
        int daysInactive = 30;
        Instant cutoff = Instant.now().minus(daysInactive, ChronoUnit.DAYS);
        pushSubscriptionRepository.deactivateStaleSubscriptions(cutoff);
    }
}
