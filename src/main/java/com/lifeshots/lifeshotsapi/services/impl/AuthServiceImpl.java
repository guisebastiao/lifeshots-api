package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.LoginRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.RegisterRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.response.AuthResponseDTO;
import com.lifeshots.lifeshotsapi.dtos.response.TokenResponseDTO;
import com.lifeshots.lifeshotsapi.dtos.response.UserResponseDTO;
import com.lifeshots.lifeshotsapi.exceptions.DuplicateEntityException;
import com.lifeshots.lifeshotsapi.exceptions.EntityNotFoundException;
import com.lifeshots.lifeshotsapi.exceptions.IncorrectCredentialsException;
import com.lifeshots.lifeshotsapi.exceptions.NicknameAlreadyUsedException;
import com.lifeshots.lifeshotsapi.mappers.UserMapper;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.UserRepository;
import com.lifeshots.lifeshotsapi.services.AuthService;
import com.lifeshots.lifeshotsapi.services.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public DefaultDTO login(LoginRequestDTO loginRequestDTO) {
        User user = this.findUserByEmail(loginRequestDTO.email());

        if (user == null || !passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new IncorrectCredentialsException("Credencias incorretas");
        }

        TokenResponseDTO tokenResponseDTO = this.tokenService.generateToken(user);
        UserResponseDTO userResponseDTO = this.userMapper.toDTO(user);
        AuthResponseDTO authResponseDTO = new AuthResponseDTO(tokenResponseDTO.token(), tokenResponseDTO.expires(), userResponseDTO);

        return new DefaultDTO("Login efetuado com sucesso", Boolean.TRUE, authResponseDTO, null, null);
    }

    @Override
    @Transactional
    public DefaultDTO register(RegisterRequestDTO registerRequestDTO) {
        User user = this.findUserByEmail(registerRequestDTO.email());

        if(user != null) {
            throw new DuplicateEntityException("Essa conta já está cadastrada");
        }

        if(this.findUserByNickname(registerRequestDTO.nickname()) != null) {
            throw new NicknameAlreadyUsedException("Esse nome de usuário já existe");
        }

        User saveUser = this.userMapper.toEntity(registerRequestDTO);
        saveUser.setPassword(passwordEncoder.encode(registerRequestDTO.password()));

        this.userRepository.save(saveUser);

        return new DefaultDTO("Sua conta foi cadastrada com sucesso", Boolean.TRUE, null, null, null);
    }

    private User findUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }
    private User findUserByNickname(String nickname) {
        return this.userRepository.findByNickname(nickname).orElse(null);
    }

}
