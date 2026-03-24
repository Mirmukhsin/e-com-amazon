package org.ecomapp.securityMS.jwtConfig.refreshToken;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO {

    @NotBlank
    private String refreshToken;
}
