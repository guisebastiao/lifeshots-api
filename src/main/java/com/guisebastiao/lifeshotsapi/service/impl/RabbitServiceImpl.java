package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.EmailQueueConfig;
import com.guisebastiao.lifeshotsapi.dto.MailDTO;
import com.guisebastiao.lifeshotsapi.service.MailService;
import com.guisebastiao.lifeshotsapi.service.RabbitService;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitServiceImpl implements RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MailService mailService;

    @Override
    public void sendMailRecoverPassword(MailDTO mailDTO) {
        rabbitTemplate.convertAndSend(EmailQueueConfig.PASSWORD_RECOVERY_QUEUE, mailDTO);
    }

    @Transactional
    @RabbitListener(queues = EmailQueueConfig.PASSWORD_RECOVERY_QUEUE)
    public void consumer(MailDTO mailDTO) {
        mailService.sendEmail(mailDTO);
    }
}
