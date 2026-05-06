package org.ecomapp.orderservice.security.config;

public record InternalPrincipal(Long userId, String token) {
}
