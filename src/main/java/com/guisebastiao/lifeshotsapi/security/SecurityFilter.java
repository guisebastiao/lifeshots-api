package com.guisebastiao.lifeshotsapi.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Value("${cookie.name.access.token}")
    private String cookieNameAccessToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = this.recoverAccessToken(request);

        Optional<DecodedJWT> decoded = this.tokenService.validateAccessToken(accessToken);

        if (decoded.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = decoded.get().getClaim("userId").asString();

        this.userRepository.findById(UUIDConverter.toUUID(userId)).ifPresent(user -> {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        filterChain.doFilter(request, response);
    }

    private String recoverAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if(cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> this.cookieNameAccessToken.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
