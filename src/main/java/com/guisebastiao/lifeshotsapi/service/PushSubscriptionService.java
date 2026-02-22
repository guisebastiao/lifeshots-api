package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;

import java.util.UUID;

public interface PushSubscriptionService {
    DefaultResponse<Void> saveSubscription(PushSubscriptionRequest dto);
    void deactivate(UUID subId);
}
