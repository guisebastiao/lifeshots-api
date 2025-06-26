package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.PagingDTO;
import com.lifeshots.lifeshotsapi.dtos.response.NotificationResponseDTO;
import com.lifeshots.lifeshotsapi.mappers.NotificationMapper;
import com.lifeshots.lifeshotsapi.models.Notification;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.NotificationRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import com.lifeshots.lifeshotsapi.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public DefaultDTO findAllNotifications(int offset, int limit) {
        User user = this.authProvider.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(offset, limit);
        Page<Notification> resultPage = this.notificationRepository.findAllNotificationBelongsAuthenticatedUser(user.getId(), pageable);

        PagingDTO pagingDTO = new PagingDTO(resultPage.getTotalElements(), resultPage.getTotalPages(), offset, limit);

        List<NotificationResponseDTO> data = resultPage.stream()
                .map(this.notificationMapper::toResponseDTO)
                .toList();

        return new DefaultDTO("Notificações encontradas", Boolean.TRUE, data, null, pagingDTO);
    }
}
