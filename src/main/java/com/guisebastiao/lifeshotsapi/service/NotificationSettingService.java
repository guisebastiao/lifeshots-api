package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.NotificationSettingRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationSettingResponse;

public interface NotificationSettingService {
    DefaultResponse<NotificationSettingResponse> notifyAllNotifications(NotificationSettingRequest.NotifyAll dto);
    DefaultResponse<NotificationSettingResponse> findNotificationSetting();
    DefaultResponse<NotificationSettingResponse> updateNotificationSetting(NotificationSettingRequest dto);
}
