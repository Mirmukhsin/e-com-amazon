package org.ecomapp.authservice.securityConfig.jwtConfig.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserIdAndRevoked(Long userId, boolean revoked);

//    Boolean existsByUserIdAndRevoked(Long userId, boolean revoked);

    Optional<RefreshToken> findByUserIdAndRevoked(Long userId, boolean revoked);

//    void deleteAllByUserId(Long userId);
}
