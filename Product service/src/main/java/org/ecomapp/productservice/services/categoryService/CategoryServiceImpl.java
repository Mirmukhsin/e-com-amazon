package org.ecomapp.productservice.services.categoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.request.CategoryRequestDTO;
import org.ecomapp.productservice.dtos.response.CategoryResponseDTO;
import org.ecomapp.productservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.productservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productservice.models.Category;
import org.ecomapp.productservice.repositories.CategoryRepository;
import org.ecomapp.productservice.utility.CategoryMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponseDTO> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository
                .findAll(pageable).map(categoryMapper::categoryToCategoryResDto);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategoriesByParentId(Long parentCategoryId) {
        return categoryRepository
                .findAllByParentId(parentCategoryId)
                .stream().map(categoryMapper::categoryToCategoryResDto).toList();
    }

    @Override
    public CategoryResponseDTO getCategory(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .map(categoryMapper::categoryToCategoryResDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    @Override
    public CategoryResponseDTO create(CategoryRequestDTO categoryRequestDTO) {

        Category category = Category.builder().name(categoryRequestDTO.getName()).build();

        if (categoryRequestDTO.getParentCategoryId() == null) {
            categoryRepository.save(category);
        } else {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

            category.setParent(parentCategory);
            categoryRepository.save(category);
        }

        return categoryMapper.categoryToCategoryResDto(category);
    }

    @Transactional
    @Override
    public CategoryResponseDTO update(Long categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(categoryRequestDTO.getName());

        if (categoryRequestDTO.getParentCategoryId() == null) {
            category.setParent(null);
            categoryRepository.save(category);
        } else {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parentCategory);
            categoryRepository.save(category);
        }
        return categoryMapper.categoryToCategoryResDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        boolean hasChildren = categoryRepository.existsByParent_Id(categoryId);

        if (hasChildren) {
            throw new ConflictException("Cannot delete category with subcategories");
        } else {
            categoryRepository.delete(category);
        }
    }
}
