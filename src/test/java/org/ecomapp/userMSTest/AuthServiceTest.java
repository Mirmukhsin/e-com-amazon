package org.ecomapp.userMSTest;

import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.securityMS.jwtConfig.JWTService;
import org.ecomapp.securityMS.jwtConfig.LogoutRequestDTO;
import org.ecomapp.securityMS.jwtConfig.refreshToken.RefreshToken;
import org.ecomapp.securityMS.jwtConfig.refreshToken.RefreshTokenRequestDTO;
import org.ecomapp.securityMS.jwtConfig.refreshToken.service.RefreshTokenService;
import org.ecomapp.userMS.dtos.request.LoginRequestDTO;
import org.ecomapp.userMS.dtos.request.RegisterRequestDTO;
import org.ecomapp.userMS.dtos.response.LoginResponseDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.enums.UserStatus;
import org.ecomapp.userMS.models.Role;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.RoleRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.ecomapp.userMS.services.authService.AuthServiceImpl;
import org.ecomapp.userMS.utility.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testBuyer;
    private Role buyerRole;
    private RefreshToken refreshToken;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        buyerRole = Role.builder()
                .id(1L)
                .name("BUYER")
                .build();

        testBuyer = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("hashedPassword")
                .fullName("Test Buyer")
                .status(UserStatus.ACTIVE)
                .roles(new ArrayList<>(List.of(buyerRole)))
                .build();

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("buyer@gmail.com")
                .fullName("Test Buyer")
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("refresh_token_abc123")
                .user(testBuyer)
                .revoked(false)
                .build();
    }

    @Test
    void register_whenValidInput() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("buyer@gmail.com");
        dto.setPassword("password123");
        dto.setFullName("Test Buyer");

        when(userRepository.existsByEmail("buyer@gmail.com")).thenReturn(false);

        when(roleRepository.findByName("BUYER")).thenReturn(Optional.of(buyerRole));

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

        when(userMapper.registerReqDtoToUser(dto)).thenReturn(testBuyer);

        when(userMapper.userToUserResponseDTO(testBuyer)).thenReturn(responseDTO);

        UserResponseDTO result = authService.register(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("buyer@gmail.com");

        verify(userRepository).save(testBuyer);
    }

    @Test
    void register_whenEmailAlreadyExists() {

        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("buyer@gmail.com");

        when(userRepository.existsByEmail("buyer@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exist");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenBuyerRoleNotFound() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail("buyer@gmail.com");
        dto.setPassword("password123");
        dto.setFullName("Test Buyer");

        when(userRepository.existsByEmail("buyer@gmail.com")).thenReturn(false);
        when(roleRepository.findByName("BUYER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void login_whenValidCredentials() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("buyer@gmail.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail("buyer@gmail.com")).thenReturn(Optional.of(testBuyer));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(testBuyer)).thenReturn("access_token_123");
        when(refreshTokenService.generateRefreshToken(testBuyer)).thenReturn(refreshToken);
        when(userMapper.userToUserResponseDTO(testBuyer)).thenReturn(responseDTO);

        LoginResponseDTO result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access_token_123");
        assertThat(result.getRefreshToken()).isEqualTo("refresh_token_abc123");
        assertThat(result.getUser().getEmail()).isEqualTo("buyer@gmail.com");
    }

    @Test
    void login_whenUserNotFound() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("notfound@gmail.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(dto)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void login_whenPasswordWrong() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("buyer@gmail.com");
        dto.setPassword("wrongpassword");

        when(userRepository.findByEmail("buyer@gmail.com")).thenReturn(Optional.of(testBuyer));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(dto)).isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    void login_whenUserBanned() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("buyer@gmail.com");
        dto.setPassword("password123");

        testBuyer.setStatus(UserStatus.BANNED);

        when(userRepository.findByEmail("buyer@gmail.com")).thenReturn(Optional.of(testBuyer));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(UnAuthorizedException.class)
                .hasMessageContaining("banned");
    }

    @Test
    void refresh_whenValidRefreshToken() {
        // Arrange
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.setRefreshToken("refresh_token_abc123");

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token("new_refresh_token_xyz")
                .user(testBuyer)
                .build();

        when(refreshTokenService.validateRefreshToken("refresh_token_abc123")).thenReturn(refreshToken);
        when(jwtService.generateToken(testBuyer)).thenReturn("new_access_token");
        when(refreshTokenService.generateRefreshToken(testBuyer)).thenReturn(newRefreshToken);
        when(userMapper.userToUserResponseDTO(testBuyer)).thenReturn(responseDTO);

        // Act
        LoginResponseDTO result = authService.refresh(dto);

        // Assert
        assertThat(result.getAccessToken()).isEqualTo("new_access_token");
        assertThat(result.getRefreshToken()).isEqualTo("new_refresh_token_xyz");

        // Verify old token was revoked
        verify(refreshTokenService).revokeRefreshToken("refresh_token_abc123");
    }

    @Test
    void refresh_whenRefreshTokenInvalid() {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.setRefreshToken("invalid_token");

        when(refreshTokenService.validateRefreshToken("invalid_token"))
                .thenThrow(new ResourceNotFoundException("Refresh token not found"));

        assertThatThrownBy(() -> authService.refresh(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        // Arrange
        LogoutRequestDTO dto = new LogoutRequestDTO();
        dto.setRefreshToken("refresh_token_abc123");

        // Act
        authService.logout(dto);

        // Assert — verify revoke was called
        verify(refreshTokenService).revokeRefreshToken("refresh_token_abc123");
    }

}
