package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface PushSubscriptionService {
    DefaultResponse<Void> subscribe(HttpServletRequest request, PushSubscriptionRequest dto);
}
