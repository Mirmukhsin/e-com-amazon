package org.ecomapp.authservice.utility;

import org.ecomapp.authservice.dtos.request.RegisterRequestDTO;
import org.ecomapp.authservice.dtos.response.UserResponseDTO;
import org.ecomapp.authservice.models.AuthUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {

    AuthUser userFromRegisterDTO(RegisterRequestDTO dto);

    UserResponseDTO userResponseFromUser(AuthUser user);
}
