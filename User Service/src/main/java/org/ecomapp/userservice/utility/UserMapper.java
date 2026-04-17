package org.ecomapp.userservice.utility;

import org.ecomapp.userservice.dtos.request.CreateUserProfileDTO;
import org.ecomapp.userservice.dtos.response.UserResponseDTO;
import org.ecomapp.userservice.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO userResponseFromUser(User user);

    User userFromCreateDTO(CreateUserProfileDTO dto);
}
