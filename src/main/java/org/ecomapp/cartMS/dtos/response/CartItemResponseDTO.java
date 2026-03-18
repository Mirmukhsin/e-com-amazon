package org.ecomapp.cartMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
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
