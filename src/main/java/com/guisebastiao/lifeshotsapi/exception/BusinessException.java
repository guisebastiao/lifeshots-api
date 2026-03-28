package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final BusinessCode code;
    private final String messageKey;
    private final List<FieldErrorResponse> details;

    public BusinessException(HttpStatus status, String messageKey, BusinessCode code) {
        super(messageKey);
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.details = null;
    }

    public BusinessException(HttpStatus status, String messageKey, BusinessCode code, List<FieldErrorResponse> details) {
        super(messageKey);
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
        this.details = details;
    }
}
