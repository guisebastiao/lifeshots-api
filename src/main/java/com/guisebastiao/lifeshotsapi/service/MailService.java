package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.MailDTO;

public interface MailService {
    void sendEmail(MailDTO mailDTO);
}
