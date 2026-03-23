package com.guisebastiao.lifeshotsapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.config.PushQueueConfig;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PushSenderServiceImpl implements PushSenderService {

    private final RabbitTemplate rabbitTemplate;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final PushService pushService;

    public PushSenderServiceImpl(RabbitTemplate rabbitTemplate, PushSubscriptionRepository pushSubscriptionRepository, ObjectMapper objectMapper, PushService pushService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.objectMapper = objectMapper;
        this.pushService = pushService;
    }

    @Override
    public void sendPush(String title, String message, UUID receiverId) {
        PushDTO dto = new PushDTO(title, message, receiverId);
        this.rabbitTemplate.convertAndSend(PushQueueConfig.PUSH_EXCHANGE, PushQueueConfig.PUSH_ROUTING_KEY, dto);
    }

    @Transactional
    @RabbitListener(queues = PushQueueConfig.PUSH_QUEUE)
    public void consumer(PushDTO dto) {
        List<PushSubscription> subs = pushSubscriptionRepository.findAllByUserId(dto.receiverId());

        if (subs.isEmpty()) return;

        String payload;

        try {
            payload = objectMapper.writeValueAsString(new PushPayload(dto.title(), dto.message()));
        } catch (Exception ignored) {
            return;
        }

        for (PushSubscription sub : subs) {
            try {
                Subscription subscription = new Subscription(sub.getEndpoint(), new Subscription.Keys(sub.getP256dh(), sub.getAuth()));

                Notification notification = new Notification(subscription, payload);

                HttpResponse response = pushService.send(notification);

                int status = response.getStatusLine().getStatusCode();

                if (status == 404 || status == 410) {
                    sub.setActive(false);
                    pushSubscriptionRepository.save(sub);
                }
            } catch (Exception ignored) {}
        }
    }

    public record PushDTO(String title, String message, UUID receiverId) {}
    public record PushPayload(String title, String message) {}
}
