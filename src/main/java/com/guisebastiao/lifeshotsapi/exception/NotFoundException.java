package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {
    public NotFoundException(String messageKey) {
        super(HttpStatus.NOT_FOUND, messageKey, BusinessCode.NOT_FOUND);
    }
}
