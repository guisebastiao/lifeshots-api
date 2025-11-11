package com.guisebastiao.lifeshotsapi.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RefreshRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LoginResponse;
import com.guisebastiao.lifeshotsapi.dto.response.RefreshResponse;
import com.guisebastiao.lifeshotsapi.dto.response.RegisterResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UserResponse;
import com.guisebastiao.lifeshotsapi.entity.NotificationSetting;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.mapper.UserMapper;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.TokenService;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public DefaultResponse<LoginResponse> login(LoginRequest dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = this.authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        UserResponse userResponse = this.userMapper.toDTO(user);

        LoginResponse data = new LoginResponse(accessToken, refreshToken, userResponse);

        return new DefaultResponse<LoginResponse>(true, "Login efetuado com sucesso", data);
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

        NotificationSetting notificationSetting = new NotificationSetting();

        User user = this.userMapper.toEntity(dto);
        user.setPassword(this.passwordEncoder.encode(dto.password()));

        Profile profile = new Profile();
        profile.setUser(user);
        user.setProfile(profile);
        user.setNotificationSetting(notificationSetting);

        User savedUser = this.userRepository.save(user);
        UserResponse userResponse = this.userMapper.toDTO(savedUser);

        RegisterResponse data = new RegisterResponse(userResponse);

        return new DefaultResponse<RegisterResponse>(true, "Cadastro concluido com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<RefreshResponse> refresh(RefreshRequest dto) {
        DecodedJWT decodedRefreshToken = this.tokenService.validateRefreshToken(dto.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão expirada, faça seu login novamente"));

        String refreshType = decodedRefreshToken.getClaim("type").asString();

        if (!"refresh".equalsIgnoreCase(refreshType)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
        }

        DecodedJWT decodedAccessToken = this.tokenService.decodeWithoutVerification(dto.accessToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token inválido"));

        String accessType = decodedAccessToken.getClaim("type").asString();

        if (!"access".equalsIgnoreCase(accessType)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token inválido");
        }

        String accessUserId = decodedAccessToken.getClaim("userId").asString();
        String refreshUserId = decodedRefreshToken.getClaim("userId").asString();

        if (accessUserId == null || refreshUserId == null || !refreshUserId.equals(accessUserId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Os tokens não pertencem ao mesmo usuário");
        }

        User user = this.userRepository.findById(UUIDConverter.toUUID(accessUserId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String newAccessToken = this.tokenService.generateAccessToken(user);

        RefreshResponse data = new RefreshResponse(newAccessToken);

        return new DefaultResponse<RefreshResponse>(true, "Sessão renovada com sucesso", data);
    }
}
