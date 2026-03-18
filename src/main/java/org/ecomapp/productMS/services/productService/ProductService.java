package org.ecomapp.productMS.services.productService;

import org.ecomapp.productMS.dtos.request.ProductRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductResponseDTO;
import org.ecomapp.productMS.enums.ProductStatus;
import org.springframework.data.domain.Page;

public interface ProductService {

    Page<ProductResponseDTO> getAllProducts(int page, int size);

    Page<ProductResponseDTO> searchProducts(int page, int size, String keyword);

    Page<ProductResponseDTO> getProductsBySeller(int page, int size, Long sellerId);

    Page<ProductResponseDTO> getProductsByCategory(int page, int size, Long categoryId);

    Page<ProductResponseDTO> getProductsByName(int page, int size, String productName);

    ProductResponseDTO getProduct(Long productId);

    ProductResponseDTO create(ProductRequestDTO productRequestDTO);

    ProductResponseDTO update(Long productId, ProductRequestDTO productRequestDTO);

    void changeProductStatus(Long productId, ProductStatus status);
}
