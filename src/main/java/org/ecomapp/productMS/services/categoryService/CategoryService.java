package org.ecomapp.productMS.services.categoryService;

import org.ecomapp.productMS.dtos.request.CategoryRequestDTO;
import org.ecomapp.productMS.dtos.response.CategoryResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    Page<CategoryResponseDTO> getAllCategories(int page, int size);

    List<CategoryResponseDTO> getAllCategoriesByParentId(Long parentCategoryId);

    CategoryResponseDTO getCategory(Long categoryId);

    CategoryResponseDTO create(CategoryRequestDTO categoryRequestDTO);

    CategoryResponseDTO update(Long categoryId, CategoryRequestDTO categoryRequestDTO);

    void deleteCategory(Long categoryId);
}
