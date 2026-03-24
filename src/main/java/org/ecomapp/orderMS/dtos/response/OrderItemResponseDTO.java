package org.ecomapp.orderMS.dtos.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {

    private Long id;
    private Long productVariantId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double totalPrice;
}
