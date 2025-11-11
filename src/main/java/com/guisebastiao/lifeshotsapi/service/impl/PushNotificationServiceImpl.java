package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.NotificationQueueConfig;
import com.guisebastiao.lifeshotsapi.dto.NotificationDTO;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.NotificationMapper;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    @Autowired
    private WebClient webClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void sendNotification(Profile sender, Profile receiver, String title, String body, NotificationType type) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setType(type);

        Notification savedNotification = this.notificationRepository.save(notification);

        if (!this.isNotifyReceiver(type, receiver)) {
            return;
        }

        PushSubscription subscription = receiver.getUser().getPushSubscription();

        if (subscription == null || subscription.getId() == null) {
            logger.warn("Usuário {} não possui token push cadastrado.", receiver.getUser().getHandle());
            return;
        }

        NotificationResponse payload = this.notificationMapper.toDTO(savedNotification);
        NotificationDTO notificationDTO = new NotificationDTO(subscription.getToken(), payload);

        this.rabbitTemplate.convertAndSend(NotificationQueueConfig.NOTIFICATION_QUEUE, notificationDTO);
    }

    @RabbitListener(queues = NotificationQueueConfig.NOTIFICATION_QUEUE)
    public void consumer(NotificationDTO dto) {
        this.sendExpoPush(dto.token(), dto.payload());
    }

    private boolean isNotifyReceiver (NotificationType type, Profile receiver) {
        NotificationSetting notificationSetting = receiver.getUser().getNotificationSetting();

        if (!notificationSetting.isNotifyAllNotifications()) {
            return false;
        }

        return switch (type) {
            case LIKE_IN_POST -> notificationSetting.isNotifyLikeInPost();
            case COMMENT_ON_POST -> notificationSetting.isNotifyCommentOnPost();
            case LIKE_IN_COMMENT -> notificationSetting.isNotifyLikeInComment();
            case LIKE_IN_COMMENT_REPLY -> notificationSetting.isNotifyLikeInCommentReply();
            case NEW_FOLLOWERS -> notificationSetting.isNotifyNewFollowers();
            case LIKE_IN_STORY -> notificationSetting.isNotifyLikeInStory();
        };
    }

    private void sendExpoPush(String token, NotificationResponse payload) {
        if (token == null || !token.startsWith("ExponentPushToken")) {
            logger.warn("Token Expo inválido: {}", token);
            return;
        }

        var requestBody = Map.of(
                "to", token,
                "sound", "default",
                "priority", "high",
                "title", payload.title(),
                "body", payload.body(),
                "data", Map.of(
                        "notificationId", payload.id(),
                        "type", payload.type(),
                        "sender", payload.sender(),
                        "read", payload.read()
                )
        );

        System.out.println(requestBody);

        webClient.post().bodyValue(requestBody).retrieve().bodyToMono(String.class)
                .onErrorResume(error -> {
                    logger.error("Erro ao enviar a notificação push", error);
                    return Mono.empty();
                })
                .subscribe();
    }
}
