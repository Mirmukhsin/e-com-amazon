package org.ecomapp.productservice.services.productService;

import org.ecomapp.productservice.dtos.request.ProductRequestDTO;
import org.ecomapp.productservice.dtos.request.UpdateProductRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductResponseDTO;
import org.ecomapp.productservice.enums.ProductStatus;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponseDTO> getAllProducts(int page, int size);

    Page<ProductResponseDTO> searchProducts(int page, int size, String keyword);

    Page<ProductResponseDTO> getProductsBySeller(int page, int size, Long sellerId);

    Page<ProductResponseDTO> getProductsByCategory(int page, int size, Long categoryId);

    Page<ProductResponseDTO> getProductsByName(int page, int size, String productName);

    ProductResponseDTO getProduct(Long productId);

    ProductResponseDTO create(Long sellerId, ProductRequestDTO productRequestDTO);

    ProductResponseDTO update(Long productId, UpdateProductRequestDTO updateProductRequestDTO);

    void changeProductStatus(Long productId, ProductStatus status);
}
