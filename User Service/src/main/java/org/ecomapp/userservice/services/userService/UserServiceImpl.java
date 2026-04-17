package org.ecomapp.userservice.services.userService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.dtos.request.CreateUserProfileDTO;
import org.ecomapp.userservice.dtos.request.UpdateProfileRequestDTO;
import org.ecomapp.userservice.dtos.response.UserResponseDTO;
import org.ecomapp.userservice.enums.UserStatus;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.userservice.models.User;
import org.ecomapp.userservice.clients.AuthMSClient;
import org.ecomapp.userservice.repositories.UserRepository;
import org.ecomapp.userservice.utility.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMSClient authMSClient;

    @Override
    public UserResponseDTO getUserById(Long userId) {

        return userRepository.findById(userId)
                .map(userMapper::userResponseFromUser)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by id: %d!".formatted(userId)));
    }


    @Override
    public UserResponseDTO updateProfile(Long userId, UpdateProfileRequestDTO updateDTO) {
        User existedUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateDTO.getFullName() != null && !updateDTO.getFullName().isBlank()) {
            existedUser.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().isBlank()) {
            existedUser.setPhone(updateDTO.getPhone());
        }

        userRepository.save(existedUser);
        return userMapper.userResponseFromUser(existedUser);
    }

//    APIs by auth service

    @Transactional
    @Override
    public void disableAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));

//        full name, phone
        user.setStatus(UserStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());

        try {
            authMSClient.disableUser(userId);
        } catch (Exception e) {
            throw new ConflictException("Failing to disable user, - rolling back - %s".formatted(e));
        }

        userRepository.save(user);
    }

    @Override
    public UserResponseDTO createUserProfile(CreateUserProfileDTO createUserProfileDTO) {
        User user = userMapper.userFromCreateDTO(createUserProfileDTO);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return userMapper.userResponseFromUser(user);
    }
}
