package org.ecomapp.orderservice.dtos.clientsDTOs;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private Double price;
    private Integer stockQuantity;
    private String productName;
    private Long sellerId;
}
