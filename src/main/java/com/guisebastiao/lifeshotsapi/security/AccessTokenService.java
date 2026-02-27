package com.guisebastiao.lifeshotsapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AccessTokenService {

    private final MessageSource messageSource;

    @Value("${jwt.access-token-duration}")
    private long accessTokenDuration;

    @Value("${jwt.access-token-secret}")
    private String accessTokenSecret;

    public AccessTokenService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Transactional
    public String createAccessToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessTokenSecret);
            Date expires = Date.from(Instant.now().plus(accessTokenDuration, ChronoUnit.SECONDS));

            return JWT.create()
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId().toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(expires)
                    .withIssuer("lifeshots")
                    .sign(algorithm);
        } catch (Exception e) {
            throw new BusinessException(BusinessHttpStatus.UNAUTHORIZED, getMessage("security.access-token-service.create-access-token"));
        }
    }

    public String validateAccessToken(String accessToken, HttpServletRequest request) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessTokenSecret);

            return JWT.require(algorithm)
                    .build()
                    .verify(accessToken)
                    .getClaim("userId").asString();
        } catch (TokenExpiredException e) {
            request.setAttribute("auth_error", "token_expired");
            return null;
        } catch (JWTVerificationException e) {
            request.setAttribute("auth_error", "invalid_token");
            return null;
        }
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
