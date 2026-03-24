package org.ecomapp.securityMS.jwtConfig.refreshToken.service;

import org.ecomapp.securityMS.jwtConfig.refreshToken.RefreshToken;
import org.ecomapp.userMS.models.User;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(User user);

    RefreshToken validateRefreshToken(String token);

    void revokeRefreshToken(String token);

    void revokeAllUserTokens(Long userId);

}
