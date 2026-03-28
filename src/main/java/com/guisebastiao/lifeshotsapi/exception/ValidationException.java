package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidationException extends BusinessException {
    public ValidationException(List<FieldErrorResponse> details) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "validation.error.message", BusinessCode.VALIDATION_ERROR, details);
    }
}
