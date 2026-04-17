package org.ecomapp.productservice.utility;

import org.ecomapp.productservice.dtos.response.CategoryResponseDTO;
import org.ecomapp.productservice.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentCategoryId", source = "parent.id")
    @Mapping(target = "parentCategoryName", source = "parent.name")
    CategoryResponseDTO categoryToCategoryResDto(Category category);
}
