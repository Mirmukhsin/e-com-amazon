package org.ecomapp.cartservice.dtos.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {

    private Long id;
    private Integer quantity;
    private LocalDateTime addedAt;
    private Long productVariantId;

    private String variantSku;
    private Double price;

    private String productName;
    private Double subtotal;
}
