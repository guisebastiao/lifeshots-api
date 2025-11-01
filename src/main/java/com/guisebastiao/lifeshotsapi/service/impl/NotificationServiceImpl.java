package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.request.NotificationRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.entity.Notification;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.mapper.NotificationMapper;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.NotificationService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public DefaultResponse<PageResponse<NotificationResponse>> findAllNotifications(PaginationFilter pagination) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Notification> resultPage = this.notificationRepository.findAllByReceiver(user.getProfile(), pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<NotificationResponse> dataResponse = resultPage.getContent().stream()
                .map(this.notificationMapper::toDTO)
                .toList();

        PageResponse<NotificationResponse> data = new PageResponse<>(dataResponse, paging);

        return new DefaultResponse<PageResponse<NotificationResponse>>(true, "Notificações retornadas com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<List<NotificationResponse>> updatedReadNotifications() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        List<Notification> notifications = this.notificationRepository.findAllByNotificationsAndNotRead(user.getProfile());

        notifications.forEach(notification -> notification.setRead(true));

        this.notificationRepository.saveAll(notifications);

        List<NotificationResponse> data = notifications.stream()
                .map(this.notificationMapper::toDTO)
                .toList();

        return new DefaultResponse<List<NotificationResponse>>(true, "Notificações atualizadas com sucesso", data);
    }

    @Override
    public DefaultResponse<Void> deleteNotification(String notificationId) {
        Notification notification = this.notificationRepository.findById(UUIDConverter.toUUID(notificationId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));

        this.notificationRepository.delete(notification);

        return new DefaultResponse<Void>(true, "Notificação excluida com sucesso", null);
    }

    @Override
    public DefaultResponse<Void> deleteNotifications(NotificationRequest dto) {
        List<Notification> notifications = this.notificationRepository.findAllNotificationsByIds(dto.ids());

        this.notificationRepository.deleteAll(notifications);

        return new DefaultResponse<Void>(true, "Notificaçães excluidas com sucesso", null);
    }
}
