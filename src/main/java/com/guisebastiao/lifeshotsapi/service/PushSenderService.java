package com.guisebastiao.lifeshotsapi.service;

import java.util.UUID;

public interface PushSenderService {
    void sendPush(String title, String message, UUID receiverId);
}
