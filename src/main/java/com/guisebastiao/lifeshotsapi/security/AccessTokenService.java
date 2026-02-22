package com.guisebastiao.lifeshotsapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.guisebastiao.lifeshotsapi.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, getMessage());
        }
    }

    public String validateAccessToken(String accessToken, HttpServletResponse response) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessTokenSecret);

            response.setHeader("X-Auth-Status", "TOKEN_VALID");

            return JWT.require(algorithm)
                    .build()
                    .verify(accessToken)
                    .getClaim("userId").asString();
        } catch (TokenExpiredException e) {
            response.setHeader("X-Auth-Status", "TOKEN_EXPIRED");
            return null;
        } catch (JWTVerificationException e) {
            response.setHeader("X-Auth-Status", "TOKEN_INVALID");
            return null;
        }
    }

    private String getMessage() {
        return messageSource.getMessage("security.access-token-service.create-access-token", null, LocaleContextHolder.getLocale());
    }
}
