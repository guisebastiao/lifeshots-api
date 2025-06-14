package com.lifeshots.lifeshotsapi.security;

import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.UserRepository;
import com.lifeshots.lifeshotsapi.services.TokenService;
import com.lifeshots.lifeshotsapi.utils.UUIDConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        String userId = this.tokenService.validateToken(token);

        if(userId != null) {
            User user = this.userRepository.findById(UUIDConverter.toUUID(userId)).orElse(null);

            if(user == null) {
                chain.doFilter(request, response);
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.split(" ")[0].equals("Bearer")) {
            return null;
        }

        return authHeader.split(" ")[1];
    }

}
