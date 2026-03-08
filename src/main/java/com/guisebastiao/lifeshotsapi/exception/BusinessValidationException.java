package com.guisebastiao.lifeshotsapi.exception;


import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import lombok.Getter;

@Getter
public class BusinessValidationException extends BusinessException {
    private final String field;

    public BusinessValidationException(BusinessHttpStatus status, String field, String message) {
        super(status, message);
        this.field = field;
    }
}