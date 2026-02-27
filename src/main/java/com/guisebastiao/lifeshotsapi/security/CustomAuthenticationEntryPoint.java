package com.guisebastiao.lifeshotsapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;

    public CustomAuthenticationEntryPoint(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String error = (String) request.getAttribute("auth_error");
        DefaultResponse<Void> responseBody = DefaultResponse.error(generateCode(error), getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }

    private String generateCode(String error) {
        if (error == null) return "SESSION_EXPIRED";

        return switch (error) {
            case "session_expired" -> "SESSION_EXPIRED";
            case "token_expired" -> "TOKEN_EXPIRED";
            case "invalid_token" -> "INVALID_TOKEN";
            default -> "UNAUTHORIZED";
        };
    }

    private String getMessage() {
        return messageSource.getMessage("security.custom-authentication-entry-point.message", null, LocaleContextHolder.getLocale());
    }
}
