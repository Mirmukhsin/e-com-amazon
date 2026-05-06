package org.ecomapp.orderservice.dtos.clientsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockRequest {
    private Long variantId;
    private Integer quantity;
}
