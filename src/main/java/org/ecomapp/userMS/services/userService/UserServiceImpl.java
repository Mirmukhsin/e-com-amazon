package org.ecomapp.userMS.services.userService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.dtos.request.ChangePasswordRequestDTO;
import org.ecomapp.userMS.dtos.request.UpdateProfileRequestDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.ecomapp.userMS.utility.UserMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;

    private final Long CURRENT_USER_ID = securityUtils.getCurrentUserId();

    @Override
    public UserResponseDTO getUserById() {
        return userRepository.findById(CURRENT_USER_ID)
                .map(userMapper::userToUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by id: %d!".formatted(CURRENT_USER_ID)));
    }

    @Override
    public UserResponseDTO updateProfile(UpdateProfileRequestDTO updateDTO) {
        User existedUser = userRepository.findById(CURRENT_USER_ID).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateDTO.getFullName() != null) {
            existedUser.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null) {
            existedUser.setPhone(updateDTO.getPhone());
        }

        userRepository.save(existedUser);
        return userMapper.userToUserResponseDTO(existedUser);
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequestDTO changePswdDTO) {
        User user = userRepository.findById(CURRENT_USER_ID).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean matches = passwordEncoder.matches(changePswdDTO.getOldPassword(), user.getPassword());
        if (!matches) {
            throw new UnAuthorizedException("Invalid credentials!");
        } else if (passwordEncoder.matches(changePswdDTO.getNewPassword(), user.getPassword())) {
            throw new ConflictException("New password cannot be same as old password");
        } else {
            user.setPassword(passwordEncoder.encode(changePswdDTO.getNewPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public void deleteAccount() {
        User user = userRepository.findById(CURRENT_USER_ID).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        userRepository.delete(user);
    }
}
