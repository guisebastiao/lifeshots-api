package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.MailRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.RabbitMailRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.RecoverPasswordRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.ResetPasswordRequestDTO;
import com.lifeshots.lifeshotsapi.exceptions.BadRequestException;
import com.lifeshots.lifeshotsapi.models.ResetPassword;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.ResetPasswordRepository;
import com.lifeshots.lifeshotsapi.repositories.UserRepository;
import com.lifeshots.lifeshotsapi.services.RabbitMailService;
import com.lifeshots.lifeshotsapi.services.ResetPasswordService;
import com.lifeshots.lifeshotsapi.utils.TokenGenerator;
import com.lifeshots.lifeshotsapi.utils.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private RabbitMailService rabbitMailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${FRONT_URL}")
    private String frontendUrl;

    @Override
    @Transactional
    public DefaultDTO recoverPassword(RecoverPasswordRequestDTO recoverPasswordRequestDTO) {
        User user = this.userRepository.findByEmail(recoverPasswordRequestDTO.email()).orElse(null);

        this.resetPasswordRepository.deleteByUserId(user.getId());

        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setUser(user);
        resetPassword.setToken(tokenGenerator.generateToken());
        resetPassword.setExpiresIn(this.getExpiresIn());

        ResetPassword saved = this.resetPasswordRepository.save(resetPassword);

        String link = this.generateLink(saved.getToken());
        String template = this.templateMail(link);

        String subject = "Recuperar Senha - LifeShots";
        MailRequestDTO mailRequestDTO = new MailRequestDTO(user.getEmail(), subject, template);
        RabbitMailRequestDTO rabbitMailRequestDTO = new RabbitMailRequestDTO(mailRequestDTO);

        rabbitMailService.producer(rabbitMailRequestDTO);

        return new DefaultDTO("Você recebeu um email para redefinir sua senha", Boolean.TRUE, null, null, null);
    }

    @Override
    @Transactional
    public DefaultDTO resetPassword(String token, ResetPasswordRequestDTO resetPasswordRequestDTO) {
        ResetPassword resetPassword = this.resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Sua senha já foi alterada"));

        if (resetPassword.getExpiresIn().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new BadRequestException("A recuperação da senha expirou");
        }

        User user = resetPassword.getUser();
        user.setPassword(this.passwordEncoder.encode(resetPasswordRequestDTO.newPassword()));
        user.setResetPassword(null);

        this.userRepository.save(user);

        this.resetPasswordRepository.delete(resetPassword);

        return new DefaultDTO("Sua senha foi recuperada com sucesso", Boolean.TRUE, null, null, null);
    }

    private String templateMail(String link) {
        Context context = new Context();
        context.setVariable("recoveryUrl", link);
        return templateEngine.process("recover-password", context);
    }

    private LocalDateTime getExpiresIn() {
        return LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
    }

    private String generateLink(String token) {
        return String.format(this.frontendUrl + "/reset-password/" + token);
    }
}
