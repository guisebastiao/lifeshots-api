package com.guisebastiao.lifeshotsapi.security.services;

import com.guisebastiao.lifeshotsapi.entity.Device;
import com.guisebastiao.lifeshotsapi.entity.RefreshToken;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.repository.DeviceRepository;
import com.guisebastiao.lifeshotsapi.repository.RefreshTokenRepository;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final AuthService authService;
    private final DeviceRepository deviceRepository;
    private final UUIDConverter uuidConverter;
    private final MessageSource messageSource;

    @Value("${jwt.refresh-token-duration}")
    private long refreshTokenDuration;

    @Value("${cookie.refresh-token.name}")
    private String cookieRefreshTokenName;

    @Value("${cookie.device-id.name}")
    private String cookieDeviceIdName;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, @Lazy AuthService authService, DeviceRepository deviceRepository, UUIDConverter uuidConverter, MessageSource messageSource) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authService = authService;
        this.deviceRepository = deviceRepository;
        this.uuidConverter = uuidConverter;
        this.messageSource = messageSource;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        String deviceIdRequest = getCookieByRequest(request, cookieDeviceIdName).orElse(null);

        Device device = new Device();

        if (deviceIdRequest != null && !deviceIdRequest.isBlank()) {
            device = deviceRepository.findById(uuidConverter.toUUID(deviceIdRequest)).orElseGet(Device::new);
        } else {
            device.setUser(user);
        }

        device.setLastAccessedAt(Instant.now());

        RefreshToken refreshToken = device.getRefreshToken();

        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setDevice(device);
        }

        refreshToken.setRefreshToken(UUID.randomUUID());
        refreshToken.setExpiresAt(Instant.now().plus(refreshTokenDuration, ChronoUnit.SECONDS));

        device.setRefreshToken(refreshToken);
        deviceRepository.save(device);

        return refreshToken;
    }

    @Transactional
    public RefreshToken validateRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshTokenRequest = getCookieByRequest(request, cookieRefreshTokenName)
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.SESSION_INVALID, getMessage("security.refresh-token-service.validate-refresh-token.not-found-refresh-token")));

        String deviceIdRequest = getCookieByRequest(request, cookieDeviceIdName)
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.SESSION_INVALID, getMessage("security.refresh-token-service.validate-refresh-token.not-found-device-id")));


        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(uuidConverter.toUUID(refreshTokenRequest))
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.SESSION_INVALID, getMessage("security.refresh-token-service.validate-refresh-token.not-found-refresh-token")));

        if (!refreshToken.getDevice().getId().equals(uuidConverter.toUUID(deviceIdRequest))) {
            throw new BusinessException(BusinessHttpStatus.SESSION_INVALID, getMessage("security.refresh-token-service.validate-refresh-token.invalid-device"));
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            authService.logout(request, response);
            throw new BusinessException(BusinessHttpStatus.SESSION_EXPIRED, getMessage("security.refresh-token-service.validate-refresh-token.unauthorized"));
        }

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public void deleteRefreshToken(HttpServletRequest request) {
        String refreshToken = getCookieByRequest(request, cookieRefreshTokenName).orElse(null);
        if (refreshToken == null) return;
        refreshTokenRepository.deleteByRefreshToken(uuidConverter.toUUID(refreshToken));
    }


    private Optional<String> getCookieByRequest(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}