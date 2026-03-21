package com.guisebastiao.lifeshotsapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.security.services.RateLimiterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitingFilter implements Filter {

    private final RateLimiterService rateLimiterService;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimiterService rateLimiterService, MessageSource messageSource, ObjectMapper objectMapper) {
        this.rateLimiterService = rateLimiterService;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (rateLimiterService.allowRequest(httpRequest)) {
            chain.doFilter(request, response);
        } else {
            rejectRequest(httpResponse);
        }
    }

    private void rejectRequest(HttpServletResponse response) throws IOException {
        DefaultResponse<Void> body = DefaultResponse.error(BusinessHttpStatus.TOO_MANY_REQUESTS.getCode(), getMessage("security.too-many-requests.message"));
        response.setStatus(BusinessHttpStatus.TOO_MANY_REQUESTS.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}