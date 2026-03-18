package org.ecomapp.userMS.services.adminService;

import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.springframework.data.domain.Page;

public interface AdminService {

    Page<UserResponseDTO> getAllUsers(int page, int size);

    void banUser(Long userId);

    void unBanUser(Long userId);
}
