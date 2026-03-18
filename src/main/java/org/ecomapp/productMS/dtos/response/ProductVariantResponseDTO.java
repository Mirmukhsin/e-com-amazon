package org.ecomapp.productMS.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class ProductVariantResponseDTO {
    private Long id;
    private String sku;
    private Double price;
    private Integer stockQuantity;
    private Map<String,String> attributes;
}
