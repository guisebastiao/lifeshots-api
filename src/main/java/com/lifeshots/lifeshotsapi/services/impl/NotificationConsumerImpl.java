package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.config.RabbitMQConfig;
import com.lifeshots.lifeshotsapi.dtos.NotificationDTO;
import com.lifeshots.lifeshotsapi.services.WebPushService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumerImpl {

    @Autowired
    private WebPushService webPushService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE_NAME)
    public void receiveNotification(NotificationDTO notificationDTO) {
        webPushService.sendNotification(notificationDTO);
    }
}
