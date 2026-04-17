package org.ecomapp.productservice.dtos.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UpdateVariantRequestDTO {

    private String sku;

    private Double price;

    private Integer stockQuantity;

    private Map<String, String> attributes;
}
