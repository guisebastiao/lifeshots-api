package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.MailDTO;
import com.guisebastiao.lifeshotsapi.dto.request.ForgotPasswordRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RecoverPasswordRequest;
import com.guisebastiao.lifeshotsapi.entity.RecoverPassword;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.RecoverPasswordRepository;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.service.RabbitService;
import com.guisebastiao.lifeshotsapi.service.RecoverPasswordService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecoverPasswordRepository recoverPasswordRepository;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public DefaultResponse<Void> forgotPassword(ForgotPasswordRequest dto) {
        Optional<User> user = this.userRepository.findUserByEmail(dto.email());

        if (user.isEmpty()) {
            return new DefaultResponse<Void>(true, "Se houver uma conta associada a este e-mail, você receberá uma mensagem com instruções para redefinir sua senha", null);
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(2); // trocar para 15 minutos
        String token = tokenGenerator.generateToken(32);

        RecoverPassword recoverPassword = new RecoverPassword();
        recoverPassword.setUser(user.get());
        recoverPassword.setToken(token);
        recoverPassword.setExpiresAt(expiresAt);
        recoverPassword.setActive(true);

        this.recoverPasswordRepository.save(recoverPassword);

        String template = this.templateMail(this.generateLink(token));

        String subject = "Recuperar Senha";
        MailDTO mailDTO = new MailDTO(dto.email(), subject, template);

        this.rabbitService.sendMailRecoverPassword(mailDTO);

        return new DefaultResponse<Void>(true, "Se houver uma conta associada a este e-mail, você receberá uma mensagem com instruções para redefinir sua senha", null);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> recoverPassword(String token, RecoverPasswordRequest dto) {
        RecoverPassword recoverPassword = this.recoverPasswordRepository.findRecoverPasswordByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link de recuperação inválido ou inexistente"));

        if (!recoverPassword.isActive() || recoverPassword.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O link de recuperação expirou ou já foi utilizado");
        }

        User user = recoverPassword.getUser();
        user.setPassword(this.passwordEncoder.encode(dto.confirmPassword()));

        recoverPassword.setActive(false);

        this.recoverPasswordRepository.save(recoverPassword);
        this.userRepository.save(user);

        return new DefaultResponse<Void>(true, "Sua senha foi recuperada com sucesso", null);
    }

    private String templateMail(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return this.templateEngine.process("recover-password-template", context);
    }

    private String generateLink(String code) {
        return String.format(this.frontendUrl + "/recover-password/" + code);
    }
}
