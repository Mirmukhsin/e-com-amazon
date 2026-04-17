package org.ecomapp.authservice.dtos.response;

import lombok.*;
import org.ecomapp.authservice.enums.UserStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private UserStatus status;
}
