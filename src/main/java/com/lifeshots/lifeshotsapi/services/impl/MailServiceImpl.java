package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.request.MailRequestDTO;
import com.lifeshots.lifeshotsapi.exceptions.BadRequestException;
import com.lifeshots.lifeshotsapi.services.MailService;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public void sendEmail(MailRequestDTO mailRequestDTO) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(mailRequestDTO.to());
            helper.setSubject(mailRequestDTO.subject());
            helper.setText(mailRequestDTO.template(), true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new BadRequestException("Falha ao enviar e-mail");
        }
    }
}
