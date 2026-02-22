package com.guisebastiao.lifeshotsapi.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class UUIDConverter {

    private final MessageSource messageSource;

    public UUIDConverter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public UUID toUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage());
        }
    }

    private String getMessage() {
        return messageSource.getMessage("util.uuid-converter.invalid-id", null, LocaleContextHolder.getLocale());
    }
}
