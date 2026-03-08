package com.guisebastiao.lifeshotsapi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
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
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }

    private String generateCode(String error) {
        if (error == null) return BusinessHttpStatus.UNAUTHENTICATED.getCode();

        return switch (error) {
            case "token_expired" -> BusinessHttpStatus.TOKEN_EXPIRED.getCode();
            case "invalid_token" -> BusinessHttpStatus.TOKEN_INVALID.getCode();
            default -> BusinessHttpStatus.SESSION_INVALID.getCode();
        };
    }

    private String getMessage() {
        return messageSource.getMessage("security.custom-authentication-entry-point.message", null, LocaleContextHolder.getLocale());
    }
}
