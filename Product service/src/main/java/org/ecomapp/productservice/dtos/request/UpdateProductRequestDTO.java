package org.ecomapp.productservice.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateProductRequestDTO {

    private String name;

    private String description;

    private Double basePrice;

    private Long categoryId;
}
