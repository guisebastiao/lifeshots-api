package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MailQueueConfig;
import com.guisebastiao.lifeshotsapi.service.MailSenderService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.mail.username}")
    private String from;

    public MailSenderServiceImpl(JavaMailSender mailSender, RabbitTemplate rabbitTemplate) {
        this.mailSender = mailSender;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendMail(String to, String subject, String template) {
        rabbitTemplate.convertAndSend(
                MailQueueConfig.MAIL_EXCHANGE,
                MailQueueConfig.MAIL_ROUTING_KEY,
                new MailDTO(to, subject, template)
        );
    }

    @RabbitListener(queues = MailQueueConfig.MAIL_QUEUE)
    public void consumer(MailDTO mailDTO) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(mailDTO.to);
            helper.setSubject(mailDTO.subject);
            helper.setText(mailDTO.template, true);

            mailSender.send(mimeMessage);
        } catch (Exception ignored) {}
    }

    private record MailDTO(String to, String subject, String template) {}
}
