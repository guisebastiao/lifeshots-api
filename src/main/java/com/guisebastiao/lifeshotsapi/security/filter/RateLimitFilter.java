package com.guisebastiao.lifeshotsapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.enums.BusinessCode;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final static int MAX_REQUESTS = 100;

    private final MessageSource messageSource;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(MAX_REQUESTS, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(MAX_REQUESTS, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            DefaultResponse<Void> responseBody = DefaultResponse.error(BusinessCode.TOO_MANY_REQUESTS.getValue(), getMessage());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        }
    }

    private String getMessage() {
        return messageSource.getMessage("security.too-many-requests.message", null, LocaleContextHolder.getLocale());
    }
}