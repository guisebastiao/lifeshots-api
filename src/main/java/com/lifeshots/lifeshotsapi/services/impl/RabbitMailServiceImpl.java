package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.config.RabbitMQConfig;
import com.lifeshots.lifeshotsapi.dtos.request.RabbitMailRequestDTO;
import com.lifeshots.lifeshotsapi.services.MailService;
import com.lifeshots.lifeshotsapi.services.RabbitMailService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMailServiceImpl implements RabbitMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MailService mailService;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.RESET_PASSWORD_QUEUE_NAME)
    public void consumer(RabbitMailRequestDTO rabbitMailRequestDTO) {
        mailService.sendEmail(rabbitMailRequestDTO.mailRequestDTO());
    }

    @Override
    @Transactional
    public void producer(RabbitMailRequestDTO rabbitMailRequestDTO) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.RESET_PASSWORD_QUEUE_NAME, rabbitMailRequestDTO);
    }
}
