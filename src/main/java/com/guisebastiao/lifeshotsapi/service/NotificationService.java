package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.NotificationRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    DefaultResponse<PageResponse<NotificationResponse>> findAllNotifications(PaginationFilter pagination);
    DefaultResponse<List<NotificationResponse>> updatedReadNotifications();
    DefaultResponse<Void> deleteNotification(String notificationId);
    DefaultResponse<Void> deleteNotifications(NotificationRequest dto);
}
