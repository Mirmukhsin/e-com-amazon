package org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;
    private String label;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
    private Boolean isDefault;
    private Long userId;
}
