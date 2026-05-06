package org.ecomapp.reviewservice.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ecomapp.reviewservice.security.TokenValidator;
import org.ecomapp.reviewservice.security.config.InternalAuthentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;

    public InternalTokenFilter(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        return path.startsWith("/actuator") || path.startsWith("/swagger");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("X-Internal-Auth");

        if (token == null) {
            response.sendError(401, "Missing token");
        }

        try {
            Claims claims = tokenValidator.parse(token);

            Integer version = claims.get("version", Integer.class);

            if (version == null || version < 1) {
                response.sendError(401, "Unsupported token version");
            }

            Long userId = claims.get("userId", Long.class);
            String tenantId = claims.get("tenantId", String.class);
            List<String> roles = ((List<?>) claims.get("roles")).stream().map(Object::toString).toList();

            List<SimpleGrantedAuthority> authorities = roles.stream().map(r -> new SimpleGrantedAuthority("Role_" + r)).toList();

            InternalAuthentication auth = new InternalAuthentication(userId, tenantId, token, authorities);

            try {
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
            } finally {
                SecurityContextHolder.clearContext();
            }
        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token Expired");
        } catch (JwtException e) {
            response.sendError(401, "Invalid token");
        }
    }
}
