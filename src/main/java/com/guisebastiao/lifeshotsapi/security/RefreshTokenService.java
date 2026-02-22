package com.guisebastiao.lifeshotsapi.security;

import com.guisebastiao.lifeshotsapi.entity.RefreshToken;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.RefreshTokenRepository;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    @Value("${jwt.refresh-token-duration}")
    private long refreshTokenDuration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
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

    public RefreshToken validateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findById(uuidConverter.toUUID(refreshToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("security.refresh-token-service.validate-refresh-token.not-found)")));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, getMessage("security.refresh-token-service.validate-refresh-token.unauthorized"));
        }

        return token;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
