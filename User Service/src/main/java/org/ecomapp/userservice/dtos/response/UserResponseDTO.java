package org.ecomapp.userservice.dtos.response;

import lombok.*;
import org.ecomapp.userservice.enums.UserStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserStatus status;
}
