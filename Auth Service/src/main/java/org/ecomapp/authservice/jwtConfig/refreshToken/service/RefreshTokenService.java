package org.ecomapp.authservice.jwtConfig.refreshToken.service;

import org.ecomapp.authservice.jwtConfig.refreshToken.RefreshToken;
import org.ecomapp.authservice.models.AuthUser;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(AuthUser user);

    RefreshToken validateRefreshToken(String token);

    void revokeRefreshToken(String token);

    void revokeAllUserTokens(Long userId);

    RefreshToken getRefreshToken(Long userId);



}
