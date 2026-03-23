package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class ValidationException extends BusinessException {
    public ValidationException(Object details) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "validation.error.message", BusinessCode.VALIDATION_ERROR, details);
    }
}
