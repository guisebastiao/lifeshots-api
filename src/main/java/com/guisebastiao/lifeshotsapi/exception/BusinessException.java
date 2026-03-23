package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final BusinessCode code;
    private final String messageKey;
    private final Object details;

    public BusinessException(HttpStatus status, String messageKey, BusinessCode code) {
        super(messageKey);
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.details = null;
    }

    public BusinessException(HttpStatus status, String messageKey, BusinessCode code, Object details) {
        super(messageKey);
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.details = details;
    }
}
