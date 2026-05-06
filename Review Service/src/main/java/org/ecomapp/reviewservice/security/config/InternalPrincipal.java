package org.ecomapp.reviewservice.security.config;

public record InternalPrincipal(Long userId, String token) {
}
