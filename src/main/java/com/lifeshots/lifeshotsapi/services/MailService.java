package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.request.MailRequestDTO;

public interface MailService {
    void sendEmail(MailRequestDTO mailRequestDTO);
}