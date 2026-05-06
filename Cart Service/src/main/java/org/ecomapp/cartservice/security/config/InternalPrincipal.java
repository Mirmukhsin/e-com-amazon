package org.ecomapp.cartservice.security.config;

public record InternalPrincipal(Long userId, String token) {
}
