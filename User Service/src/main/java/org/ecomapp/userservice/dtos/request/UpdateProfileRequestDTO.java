package org.ecomapp.userservice.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequestDTO {

    private String fullName;
    private String phone;
    //    private String profileImage;
}
