package org.ecomapp.userservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.ecomapp.userservice.enums.Label;

@Getter
@Setter
public class AddressRequestDTO {

    @NotNull
    private Label label;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String zip;

    @NotBlank
    private String country;

    @NotNull
    private Boolean isDefault;
}
