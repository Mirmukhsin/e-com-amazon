package org.ecomapp.productservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.ProductVariantDTO;
import org.ecomapp.productservice.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productservice.dtos.request.ReserveStockRequestDTO;
import org.ecomapp.productservice.dtos.request.UpdateVariantRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductVariantResponseDTO;
import org.ecomapp.productservice.services.productVariantService.ProductVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products/variants")
@RequiredArgsConstructor
@Tag(name = "9. Product Variants", description = "Product variant management")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @Operation(summary = "Get product variants")
    @GetMapping("/byProduct/{productId}")
    public ResponseEntity<List<ProductVariantResponseDTO>> getProductVariants(@PathVariable Long productId) {
        return new ResponseEntity<>(productVariantService.getProductVariants(productId), HttpStatus.OK);
    }

    @Operation(summary = "Get product variant")
    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponseDTO> getVariant(@PathVariable Long variantId) {
        return new ResponseEntity<>(productVariantService.getVariant(variantId), HttpStatus.OK);
    }

    @Operation(summary = "Add product variant")
    @PostMapping("/{productId}")
    public ResponseEntity<ProductVariantResponseDTO> addVariant(@PathVariable Long productId, @Valid @RequestBody ProductVariantRequestDTO productVariantRequestDTO) {
        return new ResponseEntity<>(productVariantService.addVariant(productId, productVariantRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update product variant")
    @PatchMapping("/{variantId}")
    public ResponseEntity<ProductVariantResponseDTO> updateVariant(@PathVariable Long variantId, @Valid @RequestBody UpdateVariantRequestDTO updateVariantRequestDTO) {
        return new ResponseEntity<>(productVariantService.updateVariant(variantId, updateVariantRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update product variant stock quantity")
    @PatchMapping("/{variantId}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long variantId, @Valid @RequestBody Integer quantity) {
        productVariantService.updateStock(variantId, quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete product variant")
    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long variantId) {
        productVariantService.deleteVariant(variantId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: Called by Order Service on checkout
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(
            @RequestBody List<ReserveStockRequestDTO> requests) {
        productVariantService.reserveStock(requests);
        return ResponseEntity.ok().build();
    }

    // TODO: Called by Order Service on cancellation or checkout failure
    @PostMapping("/release")
    public ResponseEntity<Void> releaseStock(
            @RequestBody List<ReserveStockRequestDTO> requests) {
        productVariantService.releaseStock(requests);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/list")
    public ResponseEntity<List<ProductVariantDTO>> getAllByIds(@RequestBody Set<Long> variantIds) {
        return new ResponseEntity<>(productVariantService.getAllByIds(variantIds), HttpStatus.OK);
    }

}
