package com.guisebastiao.lifeshotsapi.scheduler;

import com.guisebastiao.lifeshotsapi.entity.RecoverPassword;
import com.guisebastiao.lifeshotsapi.repository.RecoverPasswordRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecoverPasswordExpired {

    private static final Logger logger = LoggerFactory.getLogger(RecoverPasswordExpired.class);

    @Autowired
    private RecoverPasswordRepository recoverPasswordRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void recoverPasswordExpired() {
        LocalDateTime now = LocalDateTime.now();

        List<RecoverPassword> recoverPasswords = this.recoverPasswordRepository.findRecoverPasswordByExpired(now);
        recoverPasswords.forEach(recoverPassword -> recoverPassword.setActive(false));

        if (!recoverPasswords.isEmpty()) {
            this.recoverPasswordRepository.saveAll(recoverPasswords);
            logger.info("Expired Tokens Disabled: {}", recoverPasswords.size());
        }
    }
}
