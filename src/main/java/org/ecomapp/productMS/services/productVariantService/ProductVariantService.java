package org.ecomapp.productMS.services.productVariantService;

import org.ecomapp.productMS.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductVariantResponseDTO;

import java.util.List;

public interface ProductVariantService {

    List<ProductVariantResponseDTO> getProductVariants(Long productId);

    ProductVariantResponseDTO getVariant(Long variantId);

    ProductVariantResponseDTO addVariant(Long productId, ProductVariantRequestDTO productVariantRequestDTO);

    ProductVariantResponseDTO updateVariant(Long variantId, ProductVariantRequestDTO productVariantRequestDTO);

    void updateStock(Long variantId, Integer quantity);

    void deleteVariant(Long variantId);
}
