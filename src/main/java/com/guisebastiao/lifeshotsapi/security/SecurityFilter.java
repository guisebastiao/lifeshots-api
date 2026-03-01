package com.guisebastiao.lifeshotsapi.security;

import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;
    private final UserRepository userRepository;
    private final UUIDConverter uuidConverter;

    @Value("${cookie.access-name}")
    private String cookieAccessName;

    @Value("${cookie.refresh-name}")
    private String cookieRefreshName;

    public SecurityFilter(RefreshTokenService refreshTokenService, AccessTokenService accessTokenService, UserRepository userRepository, UUIDConverter uuidConverter) {
        this.refreshTokenService = refreshTokenService;
        this.accessTokenService = accessTokenService;
        this.userRepository = userRepository;
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> accessToken = recoverToken(request, cookieAccessName);
        Optional<String> refreshToken = recoverToken(request, cookieRefreshName);

        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        refreshTokenService.validateRefreshToken(refreshToken.get(), response);
        String userId = accessTokenService.validateAccessToken(accessToken.get(), request);

        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        userRepository.findById(uuidConverter.toUUID(userId)).ifPresent(user -> {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> recoverToken(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
