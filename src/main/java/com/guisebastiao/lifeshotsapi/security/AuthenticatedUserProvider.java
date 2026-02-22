package com.guisebastiao.lifeshotsapi.security;

import com.guisebastiao.lifeshotsapi.entity.User;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticatedUserProvider {

    private final MessageSource messageSource;

    public AuthenticatedUserProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, getMessage("security.authentication-user-provider.authentication-not-found"));
        }

        Object principal = authentication.getPrincipal();


        if (principal instanceof User user) {
            return user;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, getMessage("security.authentication-user-provider.invalid-identifier"));
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
