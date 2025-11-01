package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.MailDTO;

public interface SendRecoverPasswordService {
    void sendMailRecoverPassword(MailDTO mailDTO);
}
