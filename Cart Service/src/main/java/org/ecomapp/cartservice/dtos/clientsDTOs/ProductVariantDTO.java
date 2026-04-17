package org.ecomapp.cartservice.dtos.clientsDTOs;

import lombok.*;

import java.util.Map;

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
    private Map<String, String> attributes;
}
