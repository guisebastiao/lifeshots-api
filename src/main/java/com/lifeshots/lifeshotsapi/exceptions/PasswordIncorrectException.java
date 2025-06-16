package com.lifeshots.lifeshotsapi.exceptions;

import lombok.Getter;

@Getter
public class PasswordIncorrectException extends RuntimeException {
    private final String field;

    public PasswordIncorrectException(String message, String field) {
        super(message);
        this.field = field;
    }
}
