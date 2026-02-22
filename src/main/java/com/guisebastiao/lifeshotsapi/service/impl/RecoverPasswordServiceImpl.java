package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.RecoverPasswordTokenParam;
import com.guisebastiao.lifeshotsapi.dto.request.ForgotPasswordRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RecoverPasswordRequest;
import com.guisebastiao.lifeshotsapi.entity.RecoverPassword;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.RecoverPasswordRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.service.MailSenderService;
import com.guisebastiao.lifeshotsapi.service.RecoverPasswordService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
    private final MessageSource messageSource;

    @Value("${frontend.url}")
    private String frontendUrl;

    public RecoverPasswordServiceImpl(UserRepository userRepository, RecoverPasswordRepository recoverPasswordRepository, MailSenderService mailSenderService, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator, TemplateEngine templateEngine, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.recoverPasswordRepository = recoverPasswordRepository;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
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

        RecoverPassword recoverPassword = new RecoverPassword();
        recoverPassword.setUser(user.get());
        recoverPassword.setToken(token);
        recoverPassword.setExpiresAt(expiresAt);
        recoverPassword.setActive(true);

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.recover-password-service.methods.recover-password.not-found")));

        if (!recoverPassword.isActive() || recoverPassword.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.recover-password-service.methods.recover-password.bad-request"));
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
        return String.format(frontendUrl + "/recover-password/" + code);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
