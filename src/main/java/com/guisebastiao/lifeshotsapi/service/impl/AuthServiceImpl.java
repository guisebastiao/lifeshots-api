package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.exception.ValidationException;
import com.guisebastiao.lifeshotsapi.mapper.AuthMapper;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final Environment environment;
    private final AuthMapper authMapper;

    @Value("${cookie.access-token.name}")
    private String cookieAccessTokenName;

    @Value("${cookie.refresh-token.name}")
    private String cookieRefreshTokenName;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService, AuthMapper authMapper, Environment environment) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.authMapper = authMapper;
        this.environment = environment;
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> login(HttpServletRequest request, HttpServletResponse response, LoginRequest dto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        String accessToken = accessTokenService.createAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        createCookie(response, cookieAccessTokenName, accessToken);
        createCookie(response, cookieRefreshTokenName, refreshToken.getRefreshToken().toString());

        return DefaultResponse.success(authMapper.authDTO(user));
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> register(RegisterRequest dto) {
        Optional<User> existsUser = userRepository.findByEmail(dto.email());

        if (existsUser.isPresent()) {
            FieldErrorResponse fieldError = new FieldErrorResponse("email", "services.auth-service.methods.register.conflict-email");
            throw new ValidationException(fieldError);
        }

        if (userRepository.existsUserByHandle(dto.handle())) {
            FieldErrorResponse fieldError = new FieldErrorResponse("handle", "services.auth-service.methods.register.conflict-handle");
            throw new ValidationException(fieldError);
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new NotFoundException("services.auth-service.methods.register.role-not-found"));

        NotificationSetting setting = NotificationSetting.builder().build();

        User user = authMapper.toEntity(dto);
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

        User savedUser = userRepository.save(user);

        return DefaultResponse.success(authMapper.authDTO(savedUser));
    }

    @Override
    @Transactional
    public DefaultResponse<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request, response);

        User user = refreshToken.getUser();

        String accessToken = accessTokenService.createAccessToken(user);

        createCookie(response, cookieAccessTokenName, accessToken);
        createCookie(response, cookieRefreshTokenName, refreshToken.getRefreshToken().toString());

        return DefaultResponse.success(authMapper.authDTO(user));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(request);
        removeCookie(response, cookieAccessTokenName);
        removeCookie(response, cookieRefreshTokenName);
        return DefaultResponse.success();
    }

    private void createCookie(HttpServletResponse response, String cookieName, String value) {
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
}
