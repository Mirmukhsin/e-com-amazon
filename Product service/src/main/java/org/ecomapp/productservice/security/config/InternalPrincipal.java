package org.ecomapp.productservice.security.config;

public record InternalPrincipal(Long userId, String token) {
}
