package org.ecomapp.productMS.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryRequestDTO {

    @NotBlank
    private String name;
    private Long parentCategoryId;
}
