package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.NotificationParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UnreadResponse;

import java.util.List;

public interface NotificationService {
    DefaultResponse<List<NotificationResponse>> findAllNotificationsByUser(NotificationParam param, PaginationParam pagination);
    DefaultResponse<UnreadResponse> findUnreadNotificationsByUser();
    DefaultResponse<NotificationResponse> findNotificationById(String notificationId);
    DefaultResponse<Void> readNotificationById(String notificationId);
    DefaultResponse<Void> readAllUnreadNotifications();
    DefaultResponse<Void> deleteNotificationById(String notificationId);
    DefaultResponse<Void> deleteNotifications(DeleteRequest dto);
}
