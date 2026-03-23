package com.guisebastiao.lifeshotsapi.security.provider;

import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUser();
        }

        throw new UnauthorizedException();
    }
}
