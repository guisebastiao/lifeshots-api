package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class PrivateProfileException extends BusinessException {
    public PrivateProfileException() {
        super(HttpStatus.FORBIDDEN, "global-exception-handler.private-profile-exception", BusinessCode.PRIVATE_PROFILE);
    }

    public PrivateProfileException(Object details) {
        super(HttpStatus.FORBIDDEN, "global-exception-handler.private-profile-exception", BusinessCode.PRIVATE_PROFILE, details);
    }
}
