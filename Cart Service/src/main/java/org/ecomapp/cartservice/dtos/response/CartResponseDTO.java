package org.ecomapp.cartservice.dtos.response;

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
    private Integer totalItems;
    private Double totalPrice;
    private List<CartItemResponseDTO> items;

}
