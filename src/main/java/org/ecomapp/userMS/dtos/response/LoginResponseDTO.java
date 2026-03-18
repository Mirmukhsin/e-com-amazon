package org.ecomapp.userMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private UserResponseDTO user;
}
