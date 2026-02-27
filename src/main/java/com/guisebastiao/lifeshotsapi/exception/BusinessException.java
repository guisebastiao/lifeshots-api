package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private final BusinessHttpStatus status;

    public BusinessException(BusinessHttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
