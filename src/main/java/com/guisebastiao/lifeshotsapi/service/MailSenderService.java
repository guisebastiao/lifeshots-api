package com.guisebastiao.lifeshotsapi.service;

public interface MailSenderService {
    void sendMail(String to, String subject, String template);
}
