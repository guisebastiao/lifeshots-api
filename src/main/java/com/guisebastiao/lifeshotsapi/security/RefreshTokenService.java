package com.guisebastiao.lifeshotsapi.security;

import com.guisebastiao.lifeshotsapi.entity.RefreshToken;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.repository.RefreshTokenRepository;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;
    private final Environment environment;

    @Value("${jwt.refresh-token-duration}")
    private long refreshTokenDuration;

    @Value("${cookie.access-name}")
    private String cookieAccessName;

    @Value("${cookie.refresh-name}")
    private String cookieRefreshName;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, MessageSource messageSource, UUIDConverter uuidConverter, Environment environment) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
        this.environment = environment;
    }

    @Transactional
    public String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenDuration));
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getRefreshToken().toString();
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String refreshToken, HttpServletResponse response) {
        RefreshToken token = refreshTokenRepository.findById(uuidConverter.toUUID(refreshToken))
                .orElseThrow(() -> {
                    logout(response);
                    return new BusinessException(BusinessHttpStatus.SESSION_EXPIRED, getMessage("security.refresh-token-service.validate-refresh-token.not-found"));
                });


        if (token.getExpiresAt().isBefore(Instant.now())) {
            logout(response);
            throw new BusinessException(BusinessHttpStatus.SESSION_EXPIRED, getMessage("security.refresh-token-service.validate-refresh-token.unauthorized"));
        }

        return token;
    }

    private void logout(HttpServletResponse response) {
        removeCookie(response, cookieAccessName);
        removeCookie(response, cookieRefreshName);
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

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles())
                .contains("prod");
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
