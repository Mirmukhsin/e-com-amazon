package org.ecomapp.productservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductVariantRequestDTO {

    @NotBlank(message = "sku is required")
    private String sku;

    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "'Stock must be at least 1")
    private Integer stockQuantity;

    @NotNull(message = "Attributes are required")
    private Map<String, String> attributes;
}
