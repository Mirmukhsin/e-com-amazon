package org.ecomapp.cartservice.security.config;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class InternalAuthentication extends AbstractAuthenticationToken {

    private final InternalPrincipal principal;

    @Getter
    private final String token;

    public InternalAuthentication(Long userId, String tenantId, String token, Collection<? extends GrantedAuthority> authorities) {

        super(authorities);
        this.principal = new InternalPrincipal(userId, token);
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }

}
