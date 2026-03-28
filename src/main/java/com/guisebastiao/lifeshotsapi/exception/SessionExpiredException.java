package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class SessionExpiredException extends BusinessException {
    public SessionExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "global-exception-handler.session-expired-exception", BusinessCode.SESSION_EXPIRED);
    }
}