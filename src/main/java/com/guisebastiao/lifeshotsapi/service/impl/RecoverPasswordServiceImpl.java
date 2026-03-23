package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.RecoverPasswordTokenParam;
import com.guisebastiao.lifeshotsapi.dto.request.ForgotPasswordRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RecoverPasswordRequest;
import com.guisebastiao.lifeshotsapi.entity.RecoverPassword;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.exception.BadRequestException;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.repository.RecoverPasswordRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.service.MailSenderService;
import com.guisebastiao.lifeshotsapi.service.RecoverPasswordService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RecoverPasswordServiceImpl implements RecoverPasswordService {

    private final UserRepository userRepository;
    private final RecoverPasswordRepository recoverPasswordRepository;
    private final MailSenderService mailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final TemplateEngine templateEngine;

    @Value("${frontend.url}")
    private String frontendUrl;

    public RecoverPasswordServiceImpl(UserRepository userRepository, RecoverPasswordRepository recoverPasswordRepository, MailSenderService mailSenderService, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator, TemplateEngine templateEngine) {
        this.userRepository = userRepository;
        this.recoverPasswordRepository = recoverPasswordRepository;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.templateEngine = templateEngine;
    }

    @Override
    @Transactional
    public DefaultResponse<Void> forgotPassword(ForgotPasswordRequest dto) {
        Optional<User> user = userRepository.findByEmail(dto.email());

        if (user.isEmpty()) {
            return DefaultResponse.success();
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String token = tokenGenerator.generateToken(32);

        RecoverPassword recoverPassword = RecoverPassword.builder()
                .user(user.get())
                .token(token)
                .expiresAt(expiresAt)
                .build();

        recoverPasswordRepository.save(recoverPassword);

        String template = templateMail(generateLink(token));
        String subject = "Recuperar Senha";

        mailSenderService.sendMail(dto.email(), subject, template);

        return DefaultResponse.success();
    }

    @Override
    @Transactional
    public DefaultResponse<Void> recoverPassword(RecoverPasswordTokenParam param, RecoverPasswordRequest dto) {
        RecoverPassword recoverPassword = recoverPasswordRepository.findRecoverPasswordByToken(param.token())
                .orElseThrow(() -> new NotFoundException("services.recover-password-service.methods.recover-password.not-found"));

        if (!recoverPassword.isActive() || recoverPassword.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("services.recover-password-service.methods.recover-password.bad-request");
        }

        User user = recoverPassword.getUser();
        user.setPassword(passwordEncoder.encode(dto.confirmPassword()));

        recoverPassword.setActive(false);

        recoverPasswordRepository.save(recoverPassword);
        userRepository.save(user);

        return DefaultResponse.success();
    }

    private String templateMail(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("recover-password-template", context);
    }

    private String generateLink(String code) {
       return UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/recover-password")
                .queryParam("token", code)
                .build()
                .encode()
                .toUriString();
    }
}
