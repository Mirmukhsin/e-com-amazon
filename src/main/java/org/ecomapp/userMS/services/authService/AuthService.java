package org.ecomapp.userMS.services.authService;

import org.ecomapp.userMS.dtos.request.LoginRequestDTO;
import org.ecomapp.userMS.dtos.request.RegisterRequestDTO;
import org.ecomapp.userMS.dtos.response.LoginResponseDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(RegisterRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);


}
