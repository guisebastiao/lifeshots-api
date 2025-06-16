package com.lifeshots.lifeshotsapi.exceptions;

public class NicknameAlreadyUsedException extends RuntimeException {
    public NicknameAlreadyUsedException(String message) {
        super(message);
    }
}
