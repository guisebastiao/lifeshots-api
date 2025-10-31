package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;

public interface PushSubscriptionService {
    DefaultResponse<Void> saveSubscription(PushSubscriptionRequest dto);
    DefaultResponse<Void> removeSubscription();
}
