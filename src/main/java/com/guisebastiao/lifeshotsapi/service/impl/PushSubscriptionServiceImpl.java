package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import com.guisebastiao.lifeshotsapi.entity.PushSubscription;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.PushSubscriptionRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    @Autowired
    private PushSubscriptionRepository pushSubscriptionRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public DefaultResponse<Void> saveSubscription(PushSubscriptionRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (managedUser.getPushSubscription() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já inscrito nas notificações push");
        }

        PushSubscription sub = new PushSubscription();
        sub.setUser(managedUser);
        sub.setToken(dto.token());

        this.pushSubscriptionRepository.save(sub);

        return new DefaultResponse<Void>(true, "Assinatura de notificações push registrada com sucesso", null);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> removeSubscription() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        PushSubscription pushSubscription = user.getPushSubscription();

        if (pushSubscription.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assinatura não encontrada");
        }

        this.pushSubscriptionRepository.delete(pushSubscription);
        user.setPushSubscription(null);
        this.userRepository.save(user);

        return new DefaultResponse<Void>(true, "Assinatura de notificações push removida com sucesso", null);
    }
}
