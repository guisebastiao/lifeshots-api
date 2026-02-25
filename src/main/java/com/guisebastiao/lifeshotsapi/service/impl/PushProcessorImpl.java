package com.guisebastiao.lifeshotsapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.service.PushProcessor;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class PushProcessorImpl implements PushProcessor {

    private final PushSubscriptionRepository repository;
    private final PushSubscriptionService pushSubscriptionService;
    private final PushService pushService;
    private final ObjectMapper objectMapper;

    public PushProcessorImpl(PushSubscriptionRepository repository, PushSubscriptionService pushSubscriptionService, PushService pushService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.pushSubscriptionService = pushSubscriptionService;
        this.pushService = pushService;
        this.objectMapper = objectMapper;
    }

    public void processPush(PushSenderServiceImpl.PushDTO dto) {
        List<PushSubscription> subs = repository.findActiveByUser(dto.receiverId());

        for (PushSubscription sub : subs) {
            try {
                Subscription subscription = new Subscription(sub.getEndpoint(), new Subscription.Keys(sub.getP256dh(), sub.getAuth()));

                String payload = objectMapper.writeValueAsString(new PushSenderServiceImpl.PushPayload(dto.title(), dto.message()));

                Notification notification = new Notification(subscription, payload);

                pushService.send(notification);

                repository.updateLastUsed(sub.getId(), Instant.now());
            } catch (Exception e) {
                pushSubscriptionService.deactivate(sub.getId());
            }
        }
    }
}
