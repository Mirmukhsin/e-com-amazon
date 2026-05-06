package org.ecomapp.authservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.authservice.clients.UserMSClient;
import org.ecomapp.authservice.dtos.request.CreateUserProfileDTO;
import org.ecomapp.authservice.dtos.request.LoginRequestDTO;
import org.ecomapp.authservice.dtos.request.LogoutRequestDTO;
import org.ecomapp.authservice.dtos.request.RegisterRequestDTO;
import org.ecomapp.authservice.dtos.response.LoginResponseDTO;
import org.ecomapp.authservice.dtos.response.UserResponseDTO;
import org.ecomapp.authservice.enums.UserStatus;
import org.ecomapp.authservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.authservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.authservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.authservice.securityConfig.jwtConfig.JWTService;
import org.ecomapp.authservice.securityConfig.jwtConfig.refreshToken.RefreshToken;
import org.ecomapp.authservice.securityConfig.jwtConfig.refreshToken.RefreshTokenRequestDTO;
import org.ecomapp.authservice.securityConfig.jwtConfig.refreshToken.service.RefreshTokenService;
import org.ecomapp.authservice.models.AuthUser;
import org.ecomapp.authservice.models.Role;
import org.ecomapp.authservice.repositories.AuthUserRepository;
import org.ecomapp.authservice.repositories.RoleRepository;
import org.ecomapp.authservice.utility.AuthUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserMapper authUserMapper;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMSClient userMSClient;

    @Transactional
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        Boolean isExist = authUserRepository.existsByEmail(registerRequestDTO.getEmail());
        if (isExist) {
            throw new ConflictException("User already exist by this email: %s".formatted(registerRequestDTO.getEmail()));
        } else {
            Role buyerRole = roleRepository.findByName("BUYER").orElseThrow(() -> new ResourceNotFoundException("BUYER role not found"));

            AuthUser user = authUserMapper.userFromRegisterDTO(registerRequestDTO);
            user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
            user.setStatus(UserStatus.ACTIVE);
            user.setRoles(List.of(buyerRole));
            authUserRepository.save(user);

            CreateUserProfileDTO createDTO = CreateUserProfileDTO.builder()
                    .email(user.getEmail())
                    .fullName(registerRequestDTO.getFullName())
                    .phone(registerRequestDTO.getPhone())
                    .roles(user.getRoles())
                    .build();

            return userMSClient.create(createDTO);
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        AuthUser user = authUserRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));
//      extra TODO: check if user already logged in
        RefreshToken refreshToken1 = refreshTokenService.getRefreshToken(user.getId());
        if (refreshToken1 != null && refreshToken1.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new ConflictException("You are already logged in!");
        }
//       extra

        boolean matches = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());

        if (!matches) {
            throw new UnAuthorizedException("Invalid credentials");
        } else if (user.getStatus() == UserStatus.DELETED) {
            throw new UnAuthorizedException("Your account has been deleted");
        } else if (user.getStatus() == UserStatus.BANNED) {
            throw new UnAuthorizedException("Your account has been banned");
        } else {

            String accessToken = jwtService.generateToken(user);

            RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);

            return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .user(authUserMapper.userResponseFromUser(user))
                    .build();
        }
    }

    @Override
    public LoginResponseDTO refresh(Long userId, RefreshTokenRequestDTO dto) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(dto.getRefreshToken());

        AuthUser user = refreshToken.getUser();

        if (!Objects.equals(user.getId(), userId)) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        refreshTokenService.revokeRefreshToken(dto.getRefreshToken());

        String newAccessToken = jwtService.generateToken(user);

        RefreshToken newRefreshToken = refreshTokenService.generateRefreshToken(user);

        return LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .user(authUserMapper.userResponseFromUser(user))
                .build();

    }

    @Override
    public void logout(LogoutRequestDTO dto) {
        refreshTokenService.revokeRefreshToken(dto.getRefreshToken());
    }


    @Transactional
    @Override
    public void disableUser(Long userId) {
        AuthUser authUser = authUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        authUser.setEmail("deleted_" + authUser.getEmail());
        authUser.setPassword(null);
        authUser.setStatus(UserStatus.DELETED);

        refreshTokenService.revokeAllUserTokens(userId);

        authUserRepository.save(authUser);
    }
}
