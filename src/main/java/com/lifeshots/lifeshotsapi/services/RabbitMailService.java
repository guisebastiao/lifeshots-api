package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.request.RabbitMailRequestDTO;

public interface RabbitMailService {
    void producer(RabbitMailRequestDTO emailConsumerDTO);
}