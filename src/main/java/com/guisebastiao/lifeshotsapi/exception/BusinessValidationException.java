package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessValidationException extends RuntimeException {

    private final String field;
    private final BusinessHttpStatus status;

    public BusinessValidationException(BusinessHttpStatus status, String field, String message) {
        super(message);
        this.field = field;
        this.status = status;
    }
}