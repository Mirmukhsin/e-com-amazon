package org.ecomapp.apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.ecomapp.apigateway.internal.InternalTokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final InternalTokenGenerator internalTokenGenerator;

    public JwtAuthenticationFilter(InternalTokenGenerator internalTokenGenerator) {
        super(Config.class);
        this.internalTokenGenerator = internalTokenGenerator;
    }

    @Override
    public GatewayFilter apply(JwtAuthenticationFilter.Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return sendUnauthorized(exchange);
            }

            try {
                String token = authHeader.substring(7);

                if (token.isEmpty()) {
                    return sendUnauthorized(exchange);
                }

                Claims claims = Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String email = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                List<String> roles = claims.get("roles", List.class);

                String internalToken = internalTokenGenerator.generate(userId, "tenantA", roles);

                ServerWebExchange modifiedExchange = exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .header("X-User-Id", String.valueOf(userId))
                                .header("X-User-Email", email)
                                .header("X-User-Roles", roles != null ? String.join(",", roles) : "")
                                .header("X-Internal-Auth", internalToken)
                                .build()
                ).build();

                return chain.filter(modifiedExchange);

            } catch (Exception e) {
                System.err.println("EXXXXXXXXXXX");
                return sendUnauthorized(exchange);
            }
        };
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Mono<Void> sendUnauthorized(ServerWebExchange exchange) {
        System.err.println("Send Unauthorized:    ?????");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}
