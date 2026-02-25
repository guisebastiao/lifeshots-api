package com.guisebastiao.lifeshotsapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final String field;
    private final HttpStatus status;
    private final String errorStatus;

    public BusinessValidationException(String field, HttpStatus status, String errorStatus, String message) {
        super(message);
        this.field = field;
        this.status = status;
        this.errorStatus = errorStatus;
    }

}