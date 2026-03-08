package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.exception.BusinessValidationException;
import com.guisebastiao.lifeshotsapi.mapper.UserMapper;
import com.guisebastiao.lifeshotsapi.repository.DeviceRepository;
import com.guisebastiao.lifeshotsapi.repository.RoleRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.provider.UserPrincipal;
import com.guisebastiao.lifeshotsapi.security.services.AccessTokenService;
import com.guisebastiao.lifeshotsapi.security.services.RefreshTokenService;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final DeviceRepository deviceRepository;
    private final MessageSource messageSource;
    private final Environment environment;
    private final UserMapper userMapper;
    private final UUIDConverter uuidConverter;

    @Value("${cookie.access-token.name}")
    private String cookieAccessTokenName;

    @Value("${cookie.refresh-token.name}")
    private String cookieRefreshTokenName;

    @Value("${cookie.device-id.name}")
    private String cookieDeviceIdName;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService, UserMapper userMapper, DeviceRepository deviceRepository, MessageSource messageSource, Environment environment, TokenGenerator tokenGenerator, UUIDConverter uuidConverter) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.deviceRepository = deviceRepository;
        this.userMapper = userMapper;
        this.messageSource = messageSource;
        this.environment = environment;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> login(HttpServletRequest request, HttpServletResponse response, LoginRequest dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = accessTokenService.createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, request);

        createDeviceIdCookie(response, refreshToken.getDevice().getId());
        createTokenAndRefreshTokenCookies(response, accessToken, refreshToken.getRefreshToken().toString());

        return DefaultResponse.success(userMapper.authDTO(user));
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> register(RegisterRequest dto) {
        Optional<User> existsUser = userRepository.findByEmail(dto.email());

        if (existsUser.isPresent()) {
            throw new BusinessValidationException(BusinessHttpStatus.VALIDATION_ERROR, "email", getMessage("services.auth-service.methods.register.conflict-email"));
        }

        if (userRepository.existsUserByHandle(dto.handle())) {
            throw new BusinessValidationException(BusinessHttpStatus.VALIDATION_ERROR, "handle", getMessage("services.auth-service.methods.register.conflict-handle"));
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.auth-service.methods.register.role-not-found")));

        NotificationSetting notificationSetting = new NotificationSetting();

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(userRole));

        Profile profile = new Profile();
        profile.setUser(user);
        profile.setFullName(dto.fullName());
        user.setProfile(profile);
        user.setNotificationSetting(notificationSetting);
        notificationSetting.setUser(user);

        User savedUser = userRepository.save(user);

        return DefaultResponse.success(userMapper.authDTO(savedUser));
    }

    @Override
    public DefaultResponse<AuthResponse> session(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(BusinessHttpStatus.UNAUTHENTICATED, getMessage("services.auth-service.methods.session.unauthenticated"));
        }

        User user = principal.getUser();

        return DefaultResponse.success(userMapper.authDTO(user));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request, response);
        String accessToken = accessTokenService.createAccessToken(refreshToken.getDevice().getUser());

        createTokenAndRefreshTokenCookies(response, accessToken, refreshToken.getRefreshToken().toString());

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(request);

        String deviceId = getCookieByRequest(request, cookieDeviceIdName).orElse(null);

        if (deviceId != null) {
            deviceRepository.deleteById(uuidConverter.toUUID(deviceId));
        }

        removeCookie(response, cookieAccessTokenName);
        removeCookie(response, cookieRefreshTokenName);
        removeCookie(response, cookieDeviceIdName);
        return DefaultResponse.success();
    }

    private void createDeviceIdCookie(HttpServletResponse response, UUID deviceId) {
        generateCookie(response, cookieDeviceIdName, deviceId.toString());
    }

    private void createTokenAndRefreshTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        generateCookie(response, cookieAccessTokenName, accessToken);
        generateCookie(response, cookieRefreshTokenName, refreshToken);
    }

    private Optional<String> getCookieByRequest(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank())
                .findFirst();
    }

    private void generateCookie(HttpServletResponse response, String cookieName, String value) {
        boolean secure = isProduction();
        ResponseCookie cookie = ResponseCookie.from(cookieName, value).httpOnly(true).secure(secure).path("/").sameSite("Lax").build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        boolean secure = isProduction();
        ResponseCookie cookie = ResponseCookie.from(cookieName, "").httpOnly(true).secure(secure).path("/").sameSite("Lax").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
