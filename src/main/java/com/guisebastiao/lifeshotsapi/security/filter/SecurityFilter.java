package com.guisebastiao.lifeshotsapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guisebastiao.lifeshotsapi.dto.response.SessionResponse;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.provider.UserPrincipal;
import com.guisebastiao.lifeshotsapi.security.services.AccessTokenService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;
    private final UserRepository userRepository;
    private final UUIDConverter uuidConverter;
    private final Environment environment;

    @Value("${cookie.access-token.name}")
    private String cookieAccessTokenName;

    @Value("${cookie.session.name}")
    private String cookieSessionName;

    public SecurityFilter(AccessTokenService accessTokenService, UserRepository userRepository, UUIDConverter uuidConverter, Environment environment) {
        this.accessTokenService = accessTokenService;
        this.userRepository = userRepository;
        this.uuidConverter = uuidConverter;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> accessToken = recoverToken(request, cookieAccessTokenName);

        if (accessToken.isEmpty()) {
            String session = new ObjectMapper().writeValueAsString(new SessionResponse(false, null));
            createCookie(response, cookieSessionName, encode(session));
            filterChain.doFilter(request, response);
            return;
        }

        String userId = accessTokenService.validateAccessToken(accessToken.get(), request);

        if (userId == null) {
            String session = new ObjectMapper().writeValueAsString(new SessionResponse(false, null));
            createCookie(response, cookieSessionName, encode(session));
            filterChain.doFilter(request, response);
            return;
        }

        userRepository.findById(uuidConverter.toUUID(userId)).ifPresent(user -> {
            UserPrincipal principal = new UserPrincipal(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> recoverToken(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void createCookie(HttpServletResponse response, String cookieName, String value) {
        boolean secure = isProduction();

        ResponseCookie cookie = ResponseCookie.from(cookieName, value)
                .httpOnly(false)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().encodeToString(value.getBytes());
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}
