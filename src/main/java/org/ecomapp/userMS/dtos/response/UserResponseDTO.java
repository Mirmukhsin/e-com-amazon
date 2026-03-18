package org.ecomapp.userMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ecomapp.userMS.enums.UserStatus;

@Getter
@Setter
@Builder
public class UserResponseDTO {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserStatus status;
}
