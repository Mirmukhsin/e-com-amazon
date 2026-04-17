package org.ecomapp.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.request.ProductImageRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductImageResponseDTO;
import org.ecomapp.productservice.services.productImageService.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/images")
@RequiredArgsConstructor
@Tag(name = "8. Product images", description = "Product image management")
public class ProductImageController {
    private final ProductImageService productImageService;

    @Operation(summary = "Get product images")
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImageResponseDTO>> getProductImages(@PathVariable Long productId) {
        return new ResponseEntity<>(productImageService.getProductImages(productId), HttpStatus.OK);
    }

    @Operation(summary = "Get product image")
    @PostMapping("/{productId}")
    public ResponseEntity<ProductImageResponseDTO> addImage(@PathVariable Long productId, @Valid @RequestBody ProductImageRequestDTO productImageRequestDTO) {
        return new ResponseEntity<>(productImageService.addImage(productId, productImageRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Change image sort order - SELLER only")
    @PatchMapping("/{imageId}/sort")
    public ResponseEntity<ProductImageResponseDTO> changeSortOrder(@PathVariable Long imageId, @Valid @RequestBody Integer sortOrder) {
        return new ResponseEntity<>(productImageService.updateSortOrder(imageId, sortOrder), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Delete product image - SELLER only")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
