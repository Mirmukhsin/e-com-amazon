package org.ecomapp.productMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productMS.dtos.request.ProductImageRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductImageResponseDTO;
import org.ecomapp.productMS.services.productImageService.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "8. Product images", description = "Product image management")
public class ProductImageController {
    private final ProductImageService productImageService;

    @Operation(summary = "Get product images")
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponseDTO>> getProductImages(@PathVariable Long productId) {
        return new ResponseEntity<>(productImageService.getProductImages(productId), HttpStatus.OK);
    }

    @Operation(summary = "Get product image")
    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductImageResponseDTO> addImage(@PathVariable Long productId, @Valid @RequestBody ProductImageRequestDTO productImageRequestDTO) {
        return new ResponseEntity<>(productImageService.addImage(productId, productImageRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Change image sort order - SELLER only")
    @PatchMapping("/images/{imageId}/sort")
    public ResponseEntity<ProductImageResponseDTO> changeSortOrder(@PathVariable Long imageId, @RequestBody Integer sortOrder) {
        return new ResponseEntity<>(productImageService.updateSortOrder(imageId, sortOrder), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Delete product image - SELLER only")
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
