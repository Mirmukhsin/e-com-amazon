package org.ecomapp.productMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productMS.dtos.request.CategoryRequestDTO;
import org.ecomapp.productMS.dtos.response.CategoryResponseDTO;
import org.ecomapp.productMS.services.categoryService.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "6. Categories", description = "Product categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(categoryService.getAllCategories(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Get sub categories")
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponseDTO>> getAllByParentId(@PathVariable Long parentId) {
        return new ResponseEntity<>(categoryService.getAllCategoriesByParentId(parentId), HttpStatus.OK);
    }

    @Operation(summary = "Get category")
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.getCategory(categoryId), HttpStatus.OK);
    }

    @Operation(summary = "Create category")
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return new ResponseEntity<>(categoryService.create(categoryRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update category")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return new ResponseEntity<>(categoryService.update(categoryId, categoryRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
