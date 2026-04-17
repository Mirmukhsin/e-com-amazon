package org.ecomapp.productservice.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private String sku;
    private Double price;
    private Integer stockQuantity;
    private String productName;
    private Long sellerId;
}
