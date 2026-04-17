package org.ecomapp.productservice.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductImageResponseDTO {
    private Long id;
    private String imageURL;
    private Integer sortOrder;
}
