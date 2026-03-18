package org.ecomapp.productMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productMS.dtos.request.ProductRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductResponseDTO;
import org.ecomapp.productMS.enums.ProductStatus;
import org.ecomapp.productMS.services.productService.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "7. Products", description = "Product catalog management")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get all products")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(productService.getAllProducts(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Search products")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProducts(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam String keyword) {
        return new ResponseEntity<>(productService.searchProducts(page, size, keyword), HttpStatus.OK);
    }

    @Operation(summary = "Get products by seller")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<ProductResponseDTO>> getAllBySeller(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @PathVariable Long sellerId) {
        return new ResponseEntity<>(productService.getProductsBySeller(page, size, sellerId), HttpStatus.OK);
    }

    @Operation(summary = "Get products by category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponseDTO>> getAllByCategory(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @PathVariable Long categoryId) {
        return new ResponseEntity<>(productService.getProductsByCategory(page, size, categoryId), HttpStatus.OK);
    }

//    @GetMapping("/search")
//    public ResponseEntity<Page<ProductResponseDTO>> getAllByName(@RequestParam(defaultValue = "0") int page,
//                                                                 @RequestParam(defaultValue = "10") int size,
//                                                                 @RequestParam String productName) {
//        return new ResponseEntity<>(productService.getProductsByName(page, size, productName), HttpStatus.OK);
//    }

    @Operation(summary = "Get product")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
    }

    @Operation(summary = "Create product - SELLER only")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return new ResponseEntity<>(productService.create(productRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update product details - SELLER only")
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long productId, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return new ResponseEntity<>(productService.update(productId, productRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update product status - SELLER only")
    @PatchMapping("/{productId}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long productId, @RequestBody ProductStatus status) {
        productService.changeProductStatus(productId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
