package org.ecomapp.cartMS.dtos.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {

    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private Integer totalItems;
    private Double totalPrice;

}
