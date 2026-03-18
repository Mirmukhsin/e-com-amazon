package org.ecomapp.userMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SellerResponseDTO {

    private Long id;

    private String storeName;

    private String storeDescription;

    private Double averageRating;

    private Long totalSales;
}
