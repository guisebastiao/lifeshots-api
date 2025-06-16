package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.UpdateAccountRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.UpdatePasswordRequestDTO;
import com.lifeshots.lifeshotsapi.exceptions.EntityNotFoundException;
import com.lifeshots.lifeshotsapi.exceptions.NicknameAlreadyUsedException;
import com.lifeshots.lifeshotsapi.exceptions.PasswordIncorrectException;
import com.lifeshots.lifeshotsapi.mappers.UserMapper;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.UserRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import com.lifeshots.lifeshotsapi.services.UserService;
import com.lifeshots.lifeshotsapi.utils.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public DefaultDTO findById(String id) {
        User user = userRepository.findById(UUIDConverter.toUUID(id))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não foi encontrado"));

        return new DefaultDTO("Usuário encontrado com sucesso", Boolean.TRUE, userMapper.toDTO(user), null, null);
    }

    @Override
    @Transactional
    public DefaultDTO updatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        User user = this.authProvider.getAuthenticatedUser();

        if(!passwordEncoder.matches(updatePasswordRequestDTO.currentPassword(), user.getPassword())) {
            throw new PasswordIncorrectException("Senha atual está incorreta", "currentPassword");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordRequestDTO.currentPassword()));

        this.userRepository.save(user);

        return new DefaultDTO("Sua senha foi atualizada com sucesso", Boolean.TRUE, null, null, null);
    }

    @Override
    @Transactional
    public DefaultDTO updateAccount(UpdateAccountRequestDTO updateAccountDTO) {
        User user = this.authProvider.getAuthenticatedUser();

        this.checkNickname(user, updateAccountDTO.nickname());

        user.setNickname(updateAccountDTO.nickname());
        user.setName(updateAccountDTO.name());
        user.setSurname(updateAccountDTO.surname());
        user.setBio(updateAccountDTO.bio());

        this.userRepository.save(user);

        return new DefaultDTO("Conta atualizada com sucesso", Boolean.TRUE, null, null, null);
    }

    @Override
    public DefaultDTO deleteAccount() {
        User user = this.authProvider.getAuthenticatedUser();
        this.userRepository.delete(user);
        return new DefaultDTO("Sua conta foi excluida com sucesso", Boolean.TRUE, null, null, null);
    }

    private void checkNickname(User user, String nickname) {
        if(!user.getNickname().equals(nickname)) {
            Boolean existsNickname = userRepository.existsByNickname(nickname);

            if(existsNickname) {
                throw new NicknameAlreadyUsedException("Esse nome de usuário já existe");
            }
        }
    }
}
