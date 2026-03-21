package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.service.ExpiredSubscribeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpiredSubscribeServiceImpl implements ExpiredSubscribeService {

    private final PushSubscriptionRepository pushSubscriptionRepository;

    public ExpiredSubscribeServiceImpl(PushSubscriptionRepository pushSubscriptionRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
    }

    @Override
    @Transactional
    public void deleteSubscribesExpired() {
        pushSubscriptionRepository.deleteAllByNotActive();
    }
}
