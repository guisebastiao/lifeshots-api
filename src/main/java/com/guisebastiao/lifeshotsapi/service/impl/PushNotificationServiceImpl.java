package com.guisebastiao.lifeshotsapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.config.NotificationQueueConfig;
import com.guisebastiao.lifeshotsapi.dto.NotificationDTO;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
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

    @Override
    public void sendNotification(NotificationDTO dto) {
        this.rabbitTemplate.convertAndSend(NotificationQueueConfig.NOTIFICATION_QUEUE, dto);
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
