package org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressSnapshotDTO {
    private String label;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
}
