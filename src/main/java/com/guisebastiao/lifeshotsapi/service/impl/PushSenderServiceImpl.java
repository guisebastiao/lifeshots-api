package com.guisebastiao.lifeshotsapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.config.PushQueueConfig;
import com.guisebastiao.lifeshotsapi.entity.Device;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.repository.DeviceRepository;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PushSenderServiceImpl implements PushSenderService {

    private final RabbitTemplate rabbitTemplate;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;
    private final PushService pushService;

    public PushSenderServiceImpl(RabbitTemplate rabbitTemplate, PushSubscriptionRepository pushSubscriptionRepository, DeviceRepository deviceRepository, ObjectMapper objectMapper, PushService pushService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.deviceRepository = deviceRepository;
        this.objectMapper = objectMapper;
        this.pushService = pushService;
    }


    @Override
    public void sendPush(String title, String message, UUID receiverId) {
        PushDTO dto = new PushDTO(title, message, receiverId);
        this.rabbitTemplate.convertAndSend(PushQueueConfig.PUSH_EXCHANGE, PushQueueConfig.PUSH_ROUTING_KEY, dto);
    }

    @RabbitListener(queues = PushQueueConfig.PUSH_QUEUE)
    public void consumer(PushDTO dto) {
        List<PushSubscription> subs = pushSubscriptionRepository.findAllByDeviceUser(dto.receiverId());

        for (PushSubscription sub : subs) {
            try {
                Subscription subscription = new Subscription(sub.getEndpoint(), new Subscription.Keys(sub.getP256dh(), sub.getAuth()));

                String payload = objectMapper.writeValueAsString(new PushSenderServiceImpl.PushPayload(dto.title(), dto.message()));

                Notification notification = new Notification(subscription, payload);

                HttpResponse response = pushService.send(notification);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 404 || statusCode == 410) {
                    Device device = sub.getDevice();
                    device.setPushSubscription(null);
                    deviceRepository.save(device);
                }
            } catch (Exception ignore) {}
        }
    }

    public record PushDTO(String title, String message, UUID receiverId) {}
    public record PushPayload(String title, String message) {}
}
