package org.ecomapp.userMS.utility;

import org.ecomapp.userMS.dtos.request.RegisterRequestDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);


    UserResponseDTO userToUserResponseDTO(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User registerReqDtoToUser(RegisterRequestDTO registerRequestDTO);
}
