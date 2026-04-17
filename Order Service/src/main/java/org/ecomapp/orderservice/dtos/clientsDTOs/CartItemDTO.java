package org.ecomapp.orderservice.dtos.clientsDTOs;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Integer quantity;
    private Long cartId;
    private Long productVariantId;
}
