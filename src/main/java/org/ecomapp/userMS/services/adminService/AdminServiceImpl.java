package org.ecomapp.userMS.services.adminService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.enums.UserStatus;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.ecomapp.userMS.utility.UserMapper;
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
        return userRepository.findAll(pageable).map(userMapper::userToUserResponseDTO);

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
