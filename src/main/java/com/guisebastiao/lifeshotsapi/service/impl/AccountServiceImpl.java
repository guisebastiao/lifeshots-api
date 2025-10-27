package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public DefaultResponse<Void> updatePassword(UpdatePasswordRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        if (!this.passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }

        user.setPassword(this.passwordEncoder.encode(dto.confirmPassword()));

        this.userRepository.save(user);

        return new DefaultResponse<Void>(true, "Senha atualizada com sucesso", null);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteAccount(DeleteAccountRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        if (!this.passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta");
        }

        this.userRepository.delete(user);

        return new DefaultResponse<Void>(true, "Sua conta foi excluida com sucesso", null);
    }
}
