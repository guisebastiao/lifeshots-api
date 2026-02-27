package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.NotificationParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UnreadResponse;
import com.guisebastiao.lifeshotsapi.entity.Notification;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.mapper.NotificationMapper;
import com.guisebastiao.lifeshotsapi.repository.NotificationRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.NotificationService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final NotificationMapper notificationMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public NotificationServiceImpl(NotificationRepository notificationRepository, AuthenticatedUserProvider authenticatedUserProvider, NotificationMapper notificationMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.notificationRepository = notificationRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.notificationMapper = notificationMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<NotificationResponse>> findAllNotificationsByUser(NotificationParam param, PaginationParam pagination) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Notification> resultPage = notificationRepository.findAllNotificationsByUserId(user.getProfile().getId(), param.read(), pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<NotificationResponse> data = resultPage.getContent().stream()
                .map(notificationMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<UnreadResponse> findUnreadNotificationsByUser() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        long notificationsUnreadCount = notificationRepository.countAllNotificationsUnreadByUserId(user.getProfile().getId());
        UnreadResponse data = new UnreadResponse(notificationsUnreadCount);

        return DefaultResponse.success(data);
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<NotificationResponse> findNotificationById(String notificationId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Notification notification = notificationRepository.findById(uuidConverter.toUUID(notificationId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.notification-service.methods.find-notification-by-id.not-found")));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new BusinessException(BusinessHttpStatus.ACCESS_DENIED, getMessage("services.notification-service.methods.find-notification-by-id.forbidden"));
        }

        return DefaultResponse.success(notificationMapper.toDTO(notification));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> readNotificationById(String notificationId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Notification notification = notificationRepository.findById(uuidConverter.toUUID(notificationId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.notification-service.methods.read-notification-by-id.not-found")));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new BusinessException(BusinessHttpStatus.ACCESS_DENIED, getMessage("services.notification-service.methods.read-notification-by-id.forbidden"));
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> readAllUnreadNotifications() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        notificationRepository.updateAllToReadByUserId(user.getId());
        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteNotificationById(String notificationId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Notification notification = notificationRepository.findById(uuidConverter.toUUID(notificationId))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.notification-service.methods.delete-notification-by-id.not-found")));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new BusinessException(BusinessHttpStatus.ACCESS_DENIED, getMessage("services.notification-service.methods.delete-notification-by-id.forbidden"));
        }

        notification.setDeleted(true);
        notificationRepository.save(notification);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteNotifications(DeleteRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        List<Notification> notifications = notificationRepository.findAllNotificationsByIdsAndUserId(dto.ids(), user.getId());

        notifications.forEach(notification -> notification.setDeleted(true));

        notificationRepository.saveAll(notifications);

        return DefaultResponse.success();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
