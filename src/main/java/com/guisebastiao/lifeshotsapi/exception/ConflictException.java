package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {
    public ConflictException(String messageKey) {
        super(HttpStatus.CONFLICT, messageKey, BusinessCode.CONFLICT);
    }

    public ConflictException(String messageKey, Object details) {
        super(HttpStatus.CONFLICT, messageKey, BusinessCode.CONFLICT, details);
    }
}
