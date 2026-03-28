package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "global-exception-handler.unauthorized-exception", BusinessCode.UNAUTHORIZED);
    }
}
