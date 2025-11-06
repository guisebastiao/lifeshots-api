package com.guisebastiao.lifeshotsapi.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.RegisterResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UserResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.mapper.UserMapper;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.TokenService;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserMapper userMapper;

    @Value("${cookie.name.access.token}")
    private String cookieNameAccessToken;

    @Value("${cookie.name.refresh.token}")
    private String cookieNameRefreshToken;

    @Override
    @Transactional
    public DefaultResponse<UserResponse> login(LoginRequest dto, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = this.authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        response.addCookie(this.generateCookie(this.cookieNameAccessToken, accessToken));
        response.addCookie(this.generateCookie(this.cookieNameRefreshToken, refreshToken));

        UserResponse data = this.userMapper.toDTO(user);

        return new DefaultResponse<UserResponse>(true, "Login efetuado com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<RegisterResponse> register(RegisterRequest dto) {
        Optional<User> existsUser = this.userRepository.findUserByEmail(dto.email());

        if (existsUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Essa conta já está cadastrada");
        }

        if (this.userRepository.existsUserByHandleIgnoreCase(dto.handle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de usuário já está em uso");
        }

        User user = this.userMapper.toEntity(dto);
        user.setPassword(this.passwordEncoder.encode(dto.password()));

        Profile profile = new Profile();
        profile.setUser(user);
        user.setProfile(profile);

        User savedUser = this.userRepository.save(user);
        UserResponse userResponse = this.userMapper.toDTO(savedUser);

        RegisterResponse data = new RegisterResponse(userResponse);

        return new DefaultResponse<RegisterResponse>(true, "Cadastro concluido com sucesso", data);
    }

    @Override
    public DefaultResponse<Void> logout(HttpServletResponse response) {
        response.addCookie(this.deleteCookie(this.cookieNameAccessToken));
        response.addCookie(this.deleteCookie(this.cookieNameRefreshToken));
        return new DefaultResponse<>(true, "Logout efetuado com sucesso", null);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = this.findCookieValue(this.cookieNameRefreshToken, request);

        if(refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida, faça o login novamente");
        }

        DecodedJWT decoded = tokenService.validateRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão expirada, faça o login novamente"));

        String userId = decoded.getClaim("userId").asString();

        User user = this.userRepository.findById(UUIDConverter.toUUID(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String newAccessToken = this.tokenService.generateAccessToken(user);

        Cookie accessTokenCookie = this.generateCookie(this.cookieNameAccessToken, newAccessToken);

        response.addCookie(accessTokenCookie);

        return new DefaultResponse<Void>(true, "Sessão renovada com sucesso", null);
    }

    private Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    private Cookie generateCookie(String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        return cookie;
    }

    private String findCookieValue(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if(cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
