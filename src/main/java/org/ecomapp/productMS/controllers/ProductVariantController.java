package org.ecomapp.productMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productMS.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductVariantResponseDTO;
import org.ecomapp.productMS.services.productVariantService.ProductVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "9. Product Variants", description = "Product variant management")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @Operation(summary = "Get product variants")
    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponseDTO>> getProductVariants(@PathVariable Long productId) {
        return new ResponseEntity<>(productVariantService.getProductVariants(productId), HttpStatus.OK);
    }

    @Operation(summary = "Get product variant")
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponseDTO> getVariant(@PathVariable Long variantId) {
        return new ResponseEntity<>(productVariantService.getVariant(variantId), HttpStatus.OK);
    }

    @Operation(summary = "Add product variant")
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariantResponseDTO> addVariant(@PathVariable Long productId, @RequestBody ProductVariantRequestDTO productVariantRequestDTO) {
        return new ResponseEntity<>(productVariantService.addVariant(productId, productVariantRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update product variant")
    @PatchMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponseDTO> updateVariant(@PathVariable Long variantId, @RequestBody ProductVariantRequestDTO productVariantRequestDTO) {
        return new ResponseEntity<>(productVariantService.updateVariant(variantId, productVariantRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update product variant stock quantity")
    @PatchMapping("/variants/{variantId}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long variantId, @RequestBody Integer quantity) {
        productVariantService.updateStock(variantId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete product variant")
    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long variantId) {
        productVariantService.deleteVariant(variantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
