package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.NotificationSettingRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationSettingResponse;
import com.guisebastiao.lifeshotsapi.entity.NotificationSetting;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.mapper.NotificationSettingMapper;
import com.guisebastiao.lifeshotsapi.repository.NotificationSettingRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.NotificationSettingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSettingServiceImpl implements NotificationSettingService {

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private NotificationSettingMapper notificationSettingMapper;

    @Override
    @Transactional
    public DefaultResponse<NotificationSettingResponse> disableAllNotifications() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        notificationSetting.disableAllNotifications();

        this.notificationSettingRepository.save(notificationSetting);

        NotificationSettingResponse data = this.notificationSettingMapper.toDTO(notificationSetting);

        return new DefaultResponse<NotificationSettingResponse>(true, "Todas as notificações foram desabilitadas", data);
    }

    @Override
    @Transactional
    public DefaultResponse<NotificationSettingResponse> enableAllNotifications() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        notificationSetting.enableAllNotifications();

        this.notificationSettingRepository.save(notificationSetting);

        NotificationSettingResponse data = this.notificationSettingMapper.toDTO(notificationSetting);

        return new DefaultResponse<NotificationSettingResponse>(true, "Todas as notificações foram habilitadas", data);
    }

    @Override
    public DefaultResponse<NotificationSettingResponse> findNotificationSetting() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        NotificationSettingResponse data = this.notificationSettingMapper.toDTO(notificationSetting);

        return new DefaultResponse<NotificationSettingResponse>(true, "Configuração das notificações foram retornadas com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<NotificationSettingResponse> updateNotificationSetting(NotificationSettingRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        this.notificationSettingMapper.updateNotificationSetting(dto, notificationSetting);

        this.notificationSettingRepository.save(notificationSetting);

        NotificationSettingResponse data = this.notificationSettingMapper.toDTO(notificationSetting);

        return new DefaultResponse<NotificationSettingResponse>(true, "Notificações foram atualizadas", data);
    }
}
