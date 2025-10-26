package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.MailDTO;

public interface RabbitService {
    void sendMailRecoverPassword(MailDTO mailDTO);
}
