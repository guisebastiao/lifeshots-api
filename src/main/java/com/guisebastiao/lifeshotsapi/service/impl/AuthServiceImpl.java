package com.guisebastiao.lifeshotsapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.dto.response.SessionResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.exception.ValidationException;
import com.guisebastiao.lifeshotsapi.mapper.UserMapper;
import com.guisebastiao.lifeshotsapi.repository.RoleRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.provider.UserPrincipal;
import com.guisebastiao.lifeshotsapi.security.services.AccessTokenService;
import com.guisebastiao.lifeshotsapi.security.services.RefreshTokenService;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final Environment environment;
    private final UserMapper userMapper;

    @Value("${cookie.access-token.name}")
    private String cookieAccessTokenName;

    @Value("${cookie.refresh-token.name}")
    private String cookieRefreshTokenName;

    @Value("${cookie.session.name}")
    private String cookieSessionName;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService, UserMapper userMapper, Environment environment) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.userMapper = userMapper;
        this.environment = environment;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> login(HttpServletRequest request, HttpServletResponse response, LoginRequest dto) throws JsonProcessingException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = accessTokenService.createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        List<String> roles = user.getRoles().stream().map(Role::getRoleName).toList();

        String session = new ObjectMapper().writeValueAsString(new SessionResponse(
                true,
                new SessionResponse.User(user.getId(), user.getHandle(), roles)
        ));

        createCookie(response, cookieAccessTokenName, accessToken, true);
        createCookie(response, cookieRefreshTokenName, refreshToken.getRefreshToken().toString(), true);
        createCookie(response, cookieSessionName, encode(session), false);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> register(RegisterRequest dto) {
        Optional<User> existsUser = userRepository.findByEmail(dto.email());

        if (existsUser.isPresent()) {
            List<FieldErrorResponse> errors = List.of(new FieldErrorResponse("email", "services.auth-service.methods.register.conflict-email"));
            throw new ValidationException(errors);
        }

        if (userRepository.existsUserByHandle(dto.handle())) {
            List<FieldErrorResponse> errors = List.of(new FieldErrorResponse("handle", "services.auth-service.methods.register.conflict-handle"));
            throw new ValidationException(errors);
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new NotFoundException("services.auth-service.methods.register.role-not-found"));

        NotificationSetting setting = NotificationSetting.builder().build();
        setting.enableAllNotifications();

        User user = userMapper.toEntity(dto);
        user.setUserLanguage(Language.PT_BR);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(userRole));

        Profile profile = Profile.builder()
                .user(user)
                .fullName(dto.fullName())
                .build();

        user.setProfile(profile);
        user.setNotificationSetting(setting);
        setting.setUser(user);

        userRepository.save(user);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request, response);

        User user = refreshToken.getUser();

        String accessToken = accessTokenService.createAccessToken(user);

        List<String> roles = user.getRoles().stream().map(Role::getRoleName).toList();

        String session = new ObjectMapper().writeValueAsString(new SessionResponse(
                true,
                new SessionResponse.User(user.getId(), user.getHandle(), roles)
        ));

        createCookie(response, cookieAccessTokenName, accessToken, true);
        createCookie(response, cookieRefreshTokenName, refreshToken.getRefreshToken().toString(), true);
        createCookie(response, cookieSessionName, encode(session), false);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(request);
        removeCookie(response, cookieAccessTokenName, true);
        removeCookie(response, cookieRefreshTokenName, true);
        removeCookie(response, cookieSessionName, false);
        return DefaultResponse.success();
    }

    private void createCookie(HttpServletResponse response, String cookieName, String value, boolean httpOnly) {
        boolean secure = isProduction();

        ResponseCookie cookie = ResponseCookie.from(cookieName, value)
                .httpOnly(httpOnly)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void removeCookie(HttpServletResponse response, String cookieName, boolean httpOnly) {
        boolean secure = isProduction();

        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(httpOnly)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().encodeToString(value.getBytes());
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}
