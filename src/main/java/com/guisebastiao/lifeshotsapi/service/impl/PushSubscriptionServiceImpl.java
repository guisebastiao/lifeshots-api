package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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
    public DefaultResponse<Void> subscribe(HttpServletRequest request, PushSubscriptionRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        PushSubscription subscription = pushSubscriptionRepository
                .findByEndpoint(dto.endpoint())
                .orElseGet(PushSubscription::new);

        subscription.setUser(user);
        subscription.setEndpoint(dto.endpoint());
        subscription.setP256dh(dto.keys().p256dh());
        subscription.setAuth(dto.keys().auth());
        subscription.setActive(true);

        pushSubscriptionRepository.save(subscription);

        return DefaultResponse.success();
    }
}
