package com.lifeshots.lifeshotsapi.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.NotificationDTO;
import com.lifeshots.lifeshotsapi.dtos.request.SubscribeWebPushRequestDTO;
import com.lifeshots.lifeshotsapi.exceptions.BadRequestException;
import com.lifeshots.lifeshotsapi.models.PushSubscription;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.PushSubscriptionRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import com.lifeshots.lifeshotsapi.services.WebPushService;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebPushServiceImpl implements WebPushService {

    @Value("${vapid.public.key}")
    private String vapidPublicKey;

    @Value("${vapid.private.key}")
    private String vapidPrivateKey;

    @Value("${mail.contact}")
    private String contactEmail;

    private PushService pushService;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private PushSubscriptionRepository pushSubscriptionRepository;

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        this.pushService = new PushService();
        this.pushService.setPublicKey(vapidPublicKey);
        this.pushService.setPrivateKey(vapidPrivateKey);
        this.pushService.setSubject("mailto:" + contactEmail);
    }

    @Override
    public void sendNotification(NotificationDTO notificationDTO) throws RuntimeException {
        if(notificationDTO.receiver() == null) return;

        PushSubscription sub = this.pushSubscriptionRepository.findByUserId(notificationDTO.receiver().id())
                .orElseThrow(() -> new BadRequestException("O usuário não foi encontrado"));

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", notificationDTO.title());
            payload.put("message", notificationDTO.message());
            payload.put("senderId", notificationDTO.sender().id());
            payload.put("type", notificationDTO.type());
            payload.put("timestamp", Instant.now().toString());

            String jsonPayload = new ObjectMapper().writeValueAsString(payload);

            Notification notification = new Notification(
                    sub.getEndpoint(),
                    sub.getP256dh(),
                    sub.getAuth(),
                    jsonPayload
            );

            pushService.send(notification);
        } catch (Exception e) {
            throw new BadRequestException("Erro ao enviar notificação.");
        }
    }

    @Override
    public DefaultDTO subscribe(SubscribeWebPushRequestDTO subscribeWebPushRequestDTO) {
        User user = this.authProvider.getAuthenticatedUser();

        this.pushSubscriptionRepository.findByUserId(user.getId()).ifPresent(pushSubscription -> {
            this.pushSubscriptionRepository.delete(pushSubscription);
        });

        PushSubscription subscription = new PushSubscription();
        subscription.setUser(user);
        subscription.setEndpoint(subscribeWebPushRequestDTO.endpoint());
        subscription.setP256dh(subscribeWebPushRequestDTO.keys().p256dh());
        subscription.setAuth(subscribeWebPushRequestDTO.keys().auth());

        this.pushSubscriptionRepository.save(subscription);

        return new DefaultDTO("Inscrição realizada com sucesso", Boolean.TRUE, null, null, null);
    }
}
