package org.ecomapp.authservice.services;

import org.ecomapp.authservice.dtos.request.LogoutRequestDTO;
import org.ecomapp.authservice.jwtConfig.refreshToken.RefreshTokenRequestDTO;
import org.ecomapp.authservice.dtos.request.LoginRequestDTO;
import org.ecomapp.authservice.dtos.request.RegisterRequestDTO;
import org.ecomapp.authservice.dtos.response.LoginResponseDTO;
import org.ecomapp.authservice.dtos.response.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    LoginResponseDTO refresh(Long userId,RefreshTokenRequestDTO dto);

    void logout(LogoutRequestDTO dto);

    void disableUser(Long userId);


}
