package org.ecomapp.cartservice.dtos.clientsDTOs;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Integer quantity;
    private Long cartId;
    private Long productVariantId;
}
