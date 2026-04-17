package org.ecomapp.userservice.dtos.request;

import lombok.*;
import org.ecomapp.userservice.models.Role;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserProfileDTO {

    private String email;

    private String fullName;

    private String phone;

    private List<Role> roles;
}
