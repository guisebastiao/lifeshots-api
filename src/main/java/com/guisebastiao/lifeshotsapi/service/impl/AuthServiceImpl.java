package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.mapper.UserMapper;
import com.guisebastiao.lifeshotsapi.repository.RefreshTokenRepository;
import com.guisebastiao.lifeshotsapi.repository.RoleRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.AccessTokenService;
import com.guisebastiao.lifeshotsapi.security.RefreshTokenService;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final MessageSource messageSource;

    @Value("${cookie.access-name}")
    private String cookieAccessName;

    @Value("${cookie.refresh-name}")
    private String cookieRefreshName;

    public AuthServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService, UserMapper userMapper, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.userMapper = userMapper;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> login(HttpServletResponse response, LoginRequest dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();

        String accessToken = accessTokenService.createAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        generateCookie(response, cookieAccessName, accessToken);
        generateCookie(response, cookieRefreshName, refreshToken);

        return DefaultResponse.success(userMapper.authDTO(user));
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> register(RegisterRequest dto) {
        Optional<User> existsUser = userRepository.findByEmail(dto.email());

        if (existsUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, getMessage("services.auth-service.methods.register.conflict-email"));
        }

        if (userRepository.existsUserByHandle(dto.handle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, getMessage("services.auth-service.methods.register.conflict-handle"));
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.auth-service.methods.register.role-not-found")));

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
    @Transactional
    public DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieByRequest(request, cookieRefreshName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.auth-service.methods.refresh.bad-request")));

        RefreshToken refreshEntity = refreshTokenService.validateRefreshToken(refreshToken);
        User user = refreshEntity.getUser();

        refreshTokenRepository.delete(refreshEntity);

        String newAccessToken = accessTokenService.createAccessToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        generateCookie(response, cookieAccessName, newAccessToken);
        generateCookie(response, cookieRefreshName, newRefreshToken);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> logout(HttpServletResponse response) {
        removeCookie(response, cookieAccessName);
        removeCookie(response, cookieRefreshName);
        return DefaultResponse.success();
    }

    private void generateCookie(HttpServletResponse response, String cookieName, String value) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void removeCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private Optional<String> getCookieByRequest(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
