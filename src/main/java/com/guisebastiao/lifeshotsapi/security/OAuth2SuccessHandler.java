package com.guisebastiao.lifeshotsapi.security;

import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-duration}")
    private int accessTokenDuration;

    @Value("${jwt.refresh-token-duration}")
    private int refreshTokenDuration;

    @Value("${cookie.access-name}")
    private String cookieAccessName;

    @Value("${cookie.refresh-name}")
    private String cookieRefreshName;

    @Value("${frontend.url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(UserRepository userRepository, AccessTokenService accessTokenService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            if (oAuth2User == null || oAuth2User.getAttribute("email") == null) {
                redirectWithError(response, "email_not_found");
                return;
            }

            String email = oAuth2User.getAttribute("email");

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                redirectWithError(response, "user_not_registered");
                return;
            }

            String accessToken = accessTokenService.createAccessToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user);

            response.addHeader("Set-Cookie", createCookie(cookieAccessName, accessToken, accessTokenDuration / 60));
            response.addHeader("Set-Cookie", createCookie(cookieRefreshName, refreshToken, refreshTokenDuration / 60));

            response.sendRedirect(frontendUrl + "/oauth/success");
        } catch (Exception ex) {
            redirectWithError(response, "oauth_internal_error");
        }
    }

    private void redirectWithError(HttpServletResponse response, String errorCode) throws IOException {
        response.sendRedirect(frontendUrl + "/oauth/error?reason=" + errorCode);
    }

    private String createCookie(String name, String value, int maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
