package org.ecomapp.productMS.utility;

import org.ecomapp.productMS.dtos.request.CategoryRequestDTO;
import org.ecomapp.productMS.dtos.response.CategoryResponseDTO;
import org.ecomapp.productMS.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentCategoryId", source = "parent.id")
    @Mapping(target = "parentCategoryName", source = "parent.name")
    CategoryResponseDTO categoryToCategoryResDto(Category category);

    Category categoryReqDtoToCategory(CategoryRequestDTO categoryRequestDTO);
}
