package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String messageKey) {
        super(HttpStatus.FORBIDDEN, messageKey, BusinessCode.ACCESS_DENIED);
    }
}
