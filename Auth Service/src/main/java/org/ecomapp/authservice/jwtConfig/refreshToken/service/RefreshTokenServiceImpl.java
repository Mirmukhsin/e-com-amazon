package org.ecomapp.authservice.jwtConfig.refreshToken.service;

import lombok.RequiredArgsConstructor;
import org.ecomapp.authservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.authservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.authservice.jwtConfig.refreshToken.RefreshToken;
import org.ecomapp.authservice.jwtConfig.refreshToken.RefreshTokenRepository;
import org.ecomapp.authservice.models.AuthUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateRefreshToken(AuthUser user) {
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

    @Override
    public RefreshToken getRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserIdAndRevoked(userId, false).orElse(null);
    }
}
