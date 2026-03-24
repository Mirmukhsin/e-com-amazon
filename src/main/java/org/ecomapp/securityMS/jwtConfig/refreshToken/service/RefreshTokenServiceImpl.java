package org.ecomapp.securityMS.jwtConfig.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.securityMS.jwtConfig.refreshToken.RefreshToken;
import org.ecomapp.securityMS.jwtConfig.refreshToken.RefreshTokenRepository;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public RefreshToken generateRefreshToken(User user) {
        refreshTokenRepository.findByUserIdAndRevoked(user.getId(), false)
                .ifPresent(existing -> {
                    existing.setRevoked(true);
                    refreshTokenRepository.save(existing);
                });


        String refreshTokenUUID = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenUUID)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(2))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        if (refreshToken.isRevoked()) {
            throw new UnAuthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnAuthorizedException("Refresh token has expired");
        }
        return refreshToken;
    }

    @Override
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

    }

    @Override
    public void revokeAllUserTokens(Long userId) {
        List<RefreshToken> activeTokens = refreshTokenRepository
                .findAllByUserIdAndRevoked(userId, false);

        if (!activeTokens.isEmpty()) {
            activeTokens.forEach(token -> token.setRevoked(true));
            refreshTokenRepository.saveAll(activeTokens);
        }
    }
}
