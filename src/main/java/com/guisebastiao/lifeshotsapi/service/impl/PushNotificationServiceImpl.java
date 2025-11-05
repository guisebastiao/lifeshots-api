package com.guisebastiao.lifeshotsapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.config.NotificationQueueConfig;
import com.guisebastiao.lifeshotsapi.dto.NotificationDTO;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.NotificationMapper;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
import jakarta.transaction.Transactional;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    @Autowired
    private PushService pushService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void sendNotification(Profile sender, Profile receiver, String title, String body, NotificationType type) {
        com.guisebastiao.lifeshotsapi.entity.Notification notification = new com.guisebastiao.lifeshotsapi.entity.Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setType(type);

        com.guisebastiao.lifeshotsapi.entity.Notification savedNotification = this.notificationRepository.save(notification);

        if (receiver.getUser().getPushSubscription() == null) {
            return;
        }

        NotificationResponse payload = this.notificationMapper.toDTO(savedNotification);

        PushSubscription sub = receiver.getUser().getPushSubscription();

        NotificationDTO notificationDTO = new NotificationDTO(sub.getEndpoint(), sub.getP256dh(), sub.getAuth(), payload);

        this.rabbitTemplate.convertAndSend(NotificationQueueConfig.NOTIFICATION_QUEUE, notificationDTO);
    }

    @RabbitListener(queues = NotificationQueueConfig.NOTIFICATION_QUEUE)
    public void consumer(NotificationDTO dto) {
        try {
            byte[] payloadBytes = objectMapper.writeValueAsString(dto.payload()).getBytes(StandardCharsets.UTF_8);
            Notification notification = new Notification(dto.endpoint(), dto.p256dh(), dto.auth(), payloadBytes);
            this.pushService.send(notification);
        } catch (Exception error) {
            logger.error("Erro ao enviar notificação push", error);
        }
    }
}
