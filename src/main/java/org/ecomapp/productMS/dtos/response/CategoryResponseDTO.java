package org.ecomapp.productMS.dtos.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Long parentCategoryId;
    private String parentCategoryName;
}
