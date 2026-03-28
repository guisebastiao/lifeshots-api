package com.guisebastiao.lifeshotsapi.exception;

import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import org.springframework.http.HttpStatus;

public class FailedDependencyException extends BusinessException {
    public FailedDependencyException() {
        super(HttpStatus.FAILED_DEPENDENCY, "global-exception-handler.failed-dependency-exception", BusinessCode.FAILED_DEPENDENCY);
    }
}
