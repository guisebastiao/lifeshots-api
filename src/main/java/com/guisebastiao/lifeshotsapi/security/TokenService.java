package com.guisebastiao.lifeshotsapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.guisebastiao.lifeshotsapi.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    @Value("${access.token.secret}")
    private String accessTokenSecret;

    @Value("${refresh.token.secret}")
    private String refreshTokenSecret;

    @Value("${access.token.duration}")
    private int accessTokenDuration;

    @Value("${refresh.token.duration}")
    private int refreshTokenDuration;

    public String generateAccessToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(this.accessTokenSecret);

        return JWT.create()
                .withClaim("type", "access")
                .withClaim("userId", user.getId().toString())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plus(accessTokenDuration, ChronoUnit.SECONDS))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public String generateRefreshToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(this.refreshTokenSecret);

        return JWT.create()
                .withClaim("type", "refresh")
                .withClaim("userId", user.getId().toString())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plus(refreshTokenDuration, ChronoUnit.SECONDS))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<DecodedJWT> validateAccessToken(String accessToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.accessTokenSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(accessToken);
            return Optional.of(decodedJWT);
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    public Optional<DecodedJWT> validateRefreshToken(String refreshToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.refreshTokenSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(refreshToken);
            return Optional.of(decodedJWT);
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    public boolean isExpired(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiration = decodedJWT.getExpiresAt();
        return expiration.before(new Date());
    }
}
