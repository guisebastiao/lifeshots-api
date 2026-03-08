package com.guisebastiao.lifeshotsapi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;

    public CustomAccessDeniedHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        DefaultResponse<Void> responseBody = DefaultResponse.error(BusinessHttpStatus.APPLICATION_ACCESS_DENIED.getCode(), getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }

    private String getMessage() {
        return messageSource.getMessage("security.custom-access-denied-handler.message", null, LocaleContextHolder.getLocale());
    }
}
