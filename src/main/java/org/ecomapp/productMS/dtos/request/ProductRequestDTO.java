package org.ecomapp.productMS.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    private Double basePrice;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
