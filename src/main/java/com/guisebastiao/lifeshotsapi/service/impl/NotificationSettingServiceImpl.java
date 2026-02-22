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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class NotificationSettingServiceImpl implements NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final NotificationSettingMapper notificationSettingMapper;

    public NotificationSettingServiceImpl(NotificationSettingRepository notificationSettingRepository, AuthenticatedUserProvider authenticatedUserProvider, NotificationSettingMapper notificationSettingMapper) {
        this.notificationSettingRepository = notificationSettingRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.notificationSettingMapper = notificationSettingMapper;
    }

    @Override
    @Transactional
    public DefaultResponse<NotificationSettingResponse> disableAllNotifications() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        notificationSetting.disableAllNotifications();

        notificationSettingRepository.save(notificationSetting);

        return DefaultResponse.success(notificationSettingMapper.toDTO(notificationSetting));
    }

    @Override
    @Transactional
    public DefaultResponse<NotificationSettingResponse> enableAllNotifications() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        notificationSetting.enableAllNotifications();

        notificationSettingRepository.save(notificationSetting);

        return DefaultResponse.success(notificationSettingMapper.toDTO(notificationSetting));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<NotificationSettingResponse> findNotificationSetting() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        return DefaultResponse.success(notificationSettingMapper.toDTO(notificationSetting));
    }

    @Override
    @Transactional
    public DefaultResponse<NotificationSettingResponse> updateNotificationSetting(NotificationSettingRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        NotificationSetting notificationSetting = user.getNotificationSetting();

        notificationSettingMapper.updateNotificationSetting(dto, notificationSetting);

        notificationSettingRepository.save(notificationSetting);

        return DefaultResponse.success(notificationSettingMapper.toDTO(notificationSetting));
    }
}
