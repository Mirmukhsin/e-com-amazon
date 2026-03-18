package org.ecomapp.productMS.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageRequestDTO {

    @NotBlank
    private String imageURL;
    private Integer sortOrder;
}
