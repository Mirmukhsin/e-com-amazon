package org.ecomapp.userservice.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SellerRequestDTO {

    private String storeName;
    private String storeDescription;
}
