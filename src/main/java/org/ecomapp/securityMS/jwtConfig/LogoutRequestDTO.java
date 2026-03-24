package org.ecomapp.securityMS.jwtConfig;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {

    @NotBlank
    private String refreshToken;
}
