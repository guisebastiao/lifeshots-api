package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.PushQueueConfig;
import com.guisebastiao.lifeshotsapi.service.PushProcessor;
import com.guisebastiao.lifeshotsapi.service.PushSenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PushSenderServiceImpl implements PushSenderService {

    private final RabbitTemplate rabbitTemplate;
    private final PushProcessor pushProcessor;

    public PushSenderServiceImpl(RabbitTemplate rabbitTemplate, PushProcessor pushProcessor) {
        this.rabbitTemplate = rabbitTemplate;
        this.pushProcessor = pushProcessor;
    }


    @Override
    public void sendPush(String title, String message, UUID receiverId) {
        PushDTO dto = new PushDTO(title, message, receiverId);
        this.rabbitTemplate.convertAndSend(PushQueueConfig.PUSH_EXCHANGE, PushQueueConfig.PUSH_ROUTING_KEY, dto);
    }

    @RabbitListener(queues = PushQueueConfig.PUSH_QUEUE)
    public void consumer(PushDTO dto) {
        pushProcessor.processPush(dto);
    }

    public record PushDTO(String title, String message, UUID receiverId) {}
    public record PushPayload(String title, String message) {}
}
