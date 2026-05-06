package org.ecomapp.apigateway.internal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class InternalTokenGenerator {

    @Value("${jwt.internalSecretKey}")
    private String internalSecretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generate(Long userId, String tenantId, List<String> roles) {
        return Jwts.builder()
                .claim("version", 1)
                .claim("userId", userId)
                .claim("tenantId", tenantId)
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(internalSecretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
