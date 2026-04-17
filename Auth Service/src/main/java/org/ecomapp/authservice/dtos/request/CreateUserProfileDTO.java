package org.ecomapp.authservice.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ecomapp.authservice.models.Role;

import java.util.List;

@Getter
@Setter
@Builder
public class CreateUserProfileDTO {

    private String email;

    private String fullName;

    private String phone;

    private List<Role> roles;
}
