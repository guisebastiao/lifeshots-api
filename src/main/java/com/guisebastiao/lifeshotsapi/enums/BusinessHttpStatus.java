package com.guisebastiao.lifeshotsapi.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessHttpStatus {
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID"),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS"),

    APPLICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "APPLICATION_ACCESS_DENIED"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED"),
    PRIVATE_PROFILE(HttpStatus.FORBIDDEN, "PRIVATE_PROFILE"),

    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE_NOT_FOUND"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),

    CONFLICT(HttpStatus.CONFLICT, "CONFLICT"),

    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR"),

    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");

    private final HttpStatus httpStatus;
    private final String code;

    BusinessHttpStatus(HttpStatus httpStatus, String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }
}
