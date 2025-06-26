package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.config.RabbitMQConfig;
import com.lifeshots.lifeshotsapi.dtos.NotificationDTO;
import com.lifeshots.lifeshotsapi.services.NotificationProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducerImpl implements NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(NotificationDTO notificationDTO) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE_NAME, notificationDTO);
    }
}
