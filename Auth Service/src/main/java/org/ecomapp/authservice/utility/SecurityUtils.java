package org.ecomapp.authservice.utility;

import lombok.RequiredArgsConstructor;
import org.ecomapp.authservice.jwtConfig.JWTService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final JWTService jwtService;

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException(" Not authenticated");
        }

        String token = (String) auth.getCredentials();

        return jwtService.extractUserId(token);

    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException("Not authenticated");
        }

        String token = (String) auth.getCredentials();

        return jwtService.extractEmail(token);
    }
}
