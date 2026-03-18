package org.ecomapp.userMS.services.userService;

import org.ecomapp.userMS.dtos.request.ChangePasswordRequestDTO;
import org.ecomapp.userMS.dtos.request.UpdateProfileRequestDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO getUserById();

    UserResponseDTO updateProfile(UpdateProfileRequestDTO updateDTO);

    void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

    void deleteAccount();
}
