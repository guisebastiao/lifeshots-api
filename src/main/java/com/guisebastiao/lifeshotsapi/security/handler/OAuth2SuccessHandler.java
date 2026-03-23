package com.guisebastiao.lifeshotsapi.security.handler;

import com.guisebastiao.lifeshotsapi.entity.RefreshToken;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import com.guisebastiao.lifeshotsapi.security.services.AccessTokenService;
import com.guisebastiao.lifeshotsapi.security.services.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final Environment environment;

    @Value("${cookie.access-token.name}")
    private String cookieAccessTokenName;

    @Value("${cookie.refresh-token.name}")
    private String cookieRefreshTokenName;

    @Value("${frontend.url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepository, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService, Environment environment) {
        this.userRepository = userRepository;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
        this.environment = environment;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            if (oAuth2User == null || oAuth2User.getAttribute("email") == null) {
                redirectWithError(response, "email_not_found");
                return;
            }

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                redirectWithError(response, email, name);
                return;
            }

            String accessToken = accessTokenService.createAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            createCookie(response, cookieAccessTokenName, accessToken);
            createCookie(response, cookieRefreshTokenName, refreshToken.getRefreshToken().toString());

            redirectWithSuccess(response);
        } catch (Exception ex) {
            redirectWithError(response, "oauth_internal_error");
        }
    }

    private void redirectWithSuccess(HttpServletResponse response) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/oauth/success")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void redirectWithError(HttpServletResponse response, String errorCode) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/oauth/error")
                .queryParam("reason", errorCode)
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void redirectWithError(HttpServletResponse response, String email, String name) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/oauth/error")
                .queryParam("reason", "user_not_registered")
                .queryParam("email", email)
                .queryParam("name", name)
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void createCookie(HttpServletResponse response, String cookieName, String value) {
        boolean secure = isProduction();

        ResponseCookie cookie = ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private boolean isProduction() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}
