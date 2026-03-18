package org.ecomapp.orderMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemResponseDTO {

    private Long id;
    private Long productVariantId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double totalPrice;
}
