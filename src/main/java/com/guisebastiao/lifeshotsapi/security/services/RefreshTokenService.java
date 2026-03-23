package com.guisebastiao.lifeshotsapi.security.services;

import com.guisebastiao.lifeshotsapi.entity.RefreshToken;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.exception.SessionExpiredException;
import com.guisebastiao.lifeshotsapi.exception.UnauthorizedException;
import com.guisebastiao.lifeshotsapi.repository.RefreshTokenRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final Environment environment;
    private final UUIDConverter uuidConverter;

    @Value("${jwt.refresh-token-duration}")
    private long refreshTokenDuration;

    @Value("${cookie.refresh-token.name}")
    private String cookieRefreshTokenName;

    @Value("${cookie.access-token.name}")
    private String cookieAccessTokenName;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AuthenticatedUserProvider authenticatedUserProvider, Environment environment, UUIDConverter uuidConverter) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.environment = environment;
        this.uuidConverter = uuidConverter;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID())
                .expiresAt(Instant.now().plus(refreshTokenDuration, ChronoUnit.SECONDS))
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public RefreshToken validateRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        String refreshToken = getCookie(request, cookieRefreshTokenName).orElse(null);

        if (refreshToken == null) {
            throw new UnauthorizedException("security.refresh-token-service.validate-refresh-token.not-found-refresh-token");
        }

        RefreshToken token = refreshTokenRepository.findByRefreshTokenAndUser(uuidConverter.toUUID(refreshToken), user)
                .orElseThrow(UnauthorizedException::new);

        if (token.getExpiresAt().isBefore(Instant.now())) {
            deleteRefreshToken(request);
            removeCookie(response, cookieAccessTokenName);
            removeCookie(response, cookieRefreshTokenName);
            throw new SessionExpiredException();
        }

        return token;
    }

    @Transactional
    public void deleteRefreshToken(HttpServletRequest request) {
        String refreshToken = getCookie(request, cookieRefreshTokenName).orElse(null);
        if (refreshToken == null) return;
        refreshTokenRepository.deleteByRefreshToken(uuidConverter.toUUID(refreshToken));
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        boolean secure = isProduction();

        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private Optional<String> getCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}