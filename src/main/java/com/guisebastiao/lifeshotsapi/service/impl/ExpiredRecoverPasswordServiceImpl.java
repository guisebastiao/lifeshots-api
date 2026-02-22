package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.entity.RecoverPassword;
import com.guisebastiao.lifeshotsapi.repository.RecoverPasswordRepository;
import com.guisebastiao.lifeshotsapi.service.ExpiredRecoverPasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpiredRecoverPasswordServiceImpl implements ExpiredRecoverPasswordService {

    private final RecoverPasswordRepository recoverPasswordRepository;

    public ExpiredRecoverPasswordServiceImpl(RecoverPasswordRepository recoverPasswordRepository) {
        this.recoverPasswordRepository = recoverPasswordRepository;
    }

    @Override
    @Transactional
    public void removeRecoverExpiredPasswords() {
        LocalDateTime now = LocalDateTime.now();

        List<RecoverPassword> recoverPasswords = recoverPasswordRepository.findRecoverPasswordByExpired(now);
        recoverPasswords.forEach(recoverPassword -> recoverPassword.setActive(false));

        if (!recoverPasswords.isEmpty()) {
            recoverPasswordRepository.saveAll(recoverPasswords);
        }
    }
}
