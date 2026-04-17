package org.ecomapp.productservice.services.productImageService;

import org.ecomapp.productservice.dtos.request.ProductImageRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductImageResponseDTO;

import java.util.List;

public interface ProductImageService {

    List<ProductImageResponseDTO> getProductImages(Long productId);

    ProductImageResponseDTO addImage(Long productId, ProductImageRequestDTO productImageRequestDTO);

    ProductImageResponseDTO updateSortOrder(Long imageId, Integer sortOrder);

    void deleteImage(Long imageId);
}
