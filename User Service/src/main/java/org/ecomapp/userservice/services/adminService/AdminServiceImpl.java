package org.ecomapp.userservice.services.adminService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.userservice.dtos.response.UserResponseDTO;
import org.ecomapp.userservice.enums.UserStatus;
import org.ecomapp.userservice.models.User;
import org.ecomapp.userservice.repositories.UserRepository;
import org.ecomapp.userservice.utility.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserResponseDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(userMapper::userResponseFromUser);

    }

    @Override
    public void banUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getStatus() == UserStatus.BANNED) {
            throw new ConflictException("User already banned");
        } else {
            user.setStatus(UserStatus.BANNED);
            userRepository.save(user);
        }
    }

    @Override
    public void unBanUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getStatus() != UserStatus.BANNED) {
            throw new ConflictException("User is not banned");
        } else {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }
}
