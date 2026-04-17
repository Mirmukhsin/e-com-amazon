package org.ecomapp.orderservice.dtos.clientsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTOForReview {

    private Long id;
    private Long subOrderId;
    private Long buyerId;
    private Long productVariantId;
}
