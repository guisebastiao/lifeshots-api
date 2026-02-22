package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public PushSubscriptionServiceImpl(PushSubscriptionRepository pushSubscriptionRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> saveSubscription(PushSubscriptionRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        pushSubscriptionRepository.findByEndpointAndUser(dto.endpoint(), user)
                .map(existing -> {
                    existing.setP256dh(dto.keys().p256dh());
                    existing.setAuth(dto.keys().auth());
                    existing.setUserAgent(dto.userAgent());
                    existing.setDeviceId(dto.deviceId());
                    existing.setActive(true);
                    existing.setLastUsedAt(Instant.now());
                    return pushSubscriptionRepository.save(existing);
                })
                .orElseGet(() -> {
                    PushSubscription sub = new PushSubscription();
                    sub.setUser(user);
                    sub.setEndpoint(dto.endpoint());
                    sub.setP256dh(dto.keys().p256dh());
                    sub.setAuth(dto.keys().auth());
                    sub.setUserAgent(dto.userAgent());
                    sub.setDeviceId(dto.deviceId());
                    sub.setLastUsedAt(Instant.now());
                    return pushSubscriptionRepository.save(sub);
                });

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public void deactivate(UUID subId) {
        pushSubscriptionRepository.findById(subId)
                .ifPresent(sub -> {
                    sub.setActive(false);
                    pushSubscriptionRepository.save(sub);
                });
    }
}
