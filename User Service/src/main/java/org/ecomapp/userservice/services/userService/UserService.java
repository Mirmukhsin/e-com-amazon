package org.ecomapp.userservice.services.userService;

import org.ecomapp.userservice.dtos.request.CreateUserProfileDTO;
import org.ecomapp.userservice.dtos.request.UpdateProfileRequestDTO;
import org.ecomapp.userservice.dtos.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO updateProfile(Long userId, UpdateProfileRequestDTO updateDTO);


    UserResponseDTO createUserProfile(CreateUserProfileDTO createUserProfileDTO);

    void disableAccount(Long userId);
}
