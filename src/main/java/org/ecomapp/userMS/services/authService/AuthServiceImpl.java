package org.ecomapp.userMS.services.authService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.securityMS.jwtConfig.JWTService;
import org.ecomapp.userMS.dtos.request.LoginRequestDTO;
import org.ecomapp.userMS.dtos.request.RegisterRequestDTO;
import org.ecomapp.userMS.dtos.response.LoginResponseDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.enums.UserStatus;
import org.ecomapp.userMS.models.Role;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.RoleRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.ecomapp.userMS.utility.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JWTService jwtService;


    @Transactional
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        Boolean isExist = userRepository.existsByEmail(registerRequestDTO.getEmail());
        if (isExist) {
            throw new ConflictException("User already exist by this email: %s".formatted(registerRequestDTO.getEmail()));
        } else {
            Role buyerRole = roleRepository.findByName("BUYER").orElseThrow(() -> new ResourceNotFoundException("BUYER role not found"));

            User user = userMapper.registerReqDtoToUser(registerRequestDTO);
            user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
            user.setStatus(UserStatus.ACTIVE);
            user.setRoles(List.of(buyerRole));
            userRepository.save(user);
            return userMapper.userToUserResponseDTO(user);
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(() -> new UnAuthorizedException("Invalid credentials"));
        boolean matches = passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());
        if (!matches) {
            throw new UnAuthorizedException("Invalid credentials");
        } else if (user.getStatus() == UserStatus.BANNED) {
            throw new ConflictException("Your account has been banned");
        } else {

            String jwtToken = jwtService.generateToken(user);

            return LoginResponseDTO.builder()
                    .token(jwtToken)
                    .tokenType("Bearer")
                    .user(userMapper.userToUserResponseDTO(user))
                    .build();
        }
    }
}
