package com.guisebastiao.lifeshotsapi.security.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
public class AccessTokenService {

    @Value("${jwt.access-token-duration}")
    private long accessTokenDuration;

    @Value("${jwt.access-token-secret}")
    private String accessTokenSecret;

    @Transactional
    public String createAccessToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessTokenSecret);
            Date expires = Date.from(Instant.now().plusSeconds(accessTokenDuration));

            return JWT.create()
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId().toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(expires)
                    .withIssuer("lifeshots")
                    .sign(algorithm);
        } catch (Exception e) {
                throw new BadRequestException("security.access-token-service.create-access-token");
        }
    }

    public String validateAccessToken(String accessToken, HttpServletRequest request) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessTokenSecret);

            return JWT.require(algorithm)
                    .build()
                    .verify(accessToken)
                    .getClaim("userId").asString();
        } catch (Exception e) {
            request.setAttribute("auth_error", "token_expired");
            return null;
        }
    }
}
