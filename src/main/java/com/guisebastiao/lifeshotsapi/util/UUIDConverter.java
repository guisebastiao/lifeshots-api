package com.guisebastiao.lifeshotsapi.util;

import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

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
            throw new BusinessException(BusinessHttpStatus.BAD_REQUEST, getMessage());
        }
    }

    private String getMessage() {
        return messageSource.getMessage("util.uuid-converter.invalid-id", null, LocaleContextHolder.getLocale());
    }
}
