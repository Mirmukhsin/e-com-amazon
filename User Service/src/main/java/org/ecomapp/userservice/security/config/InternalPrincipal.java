package org.ecomapp.userservice.security.config;

public record InternalPrincipal(Long userId, String token) {
}
