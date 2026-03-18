package org.ecomapp.productMS.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductVariantRequestDTO {

    @NotBlank
    private String sku;

    @NotNull
    private Double price;

    @NotNull
    private Integer stockQuantity;

    @NotNull(message = "Attributes are required")
    private Map<String,String> attributes;
}
