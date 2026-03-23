package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {
    public BadRequestException(String messageKey) {
        super(HttpStatus.BAD_REQUEST, messageKey, BusinessCode.BAD_REQUEST);
    }

    public BadRequestException(String messageKey, Object details) {
        super(HttpStatus.BAD_REQUEST, messageKey, BusinessCode.BAD_REQUEST, details);
    }
}
