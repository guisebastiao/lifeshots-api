package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.service.impl.PushSenderServiceImpl;

public interface PushProcessor {
    void processPush(PushSenderServiceImpl.PushDTO dto);
}
