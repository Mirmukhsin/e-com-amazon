package org.ecomapp.paymentservice.security.config;

public record InternalPrincipal(Long userId, String token) {
}
