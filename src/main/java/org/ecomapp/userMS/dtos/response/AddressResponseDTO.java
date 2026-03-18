package org.ecomapp.userMS.dtos.response;

import lombok.*;
import org.ecomapp.userMS.enums.Label;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDTO {
    private Long id;
    private Label label;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
    private Boolean isDefault;
}
