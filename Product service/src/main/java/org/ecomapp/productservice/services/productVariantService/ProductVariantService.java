package org.ecomapp.productservice.services.productVariantService;

import org.ecomapp.productservice.dtos.ProductVariantDTO;
import org.ecomapp.productservice.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productservice.dtos.request.StockRequestDTO;
import org.ecomapp.productservice.dtos.request.UpdateVariantRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductVariantResponseDTO;

import java.util.List;
import java.util.Set;

public interface ProductVariantService {

    List<ProductVariantDTO> getAllByIds(Set<Long> variantIds);

    List<ProductVariantResponseDTO> getProductVariants(Long productId);

    ProductVariantResponseDTO getVariant(Long variantId);

    ProductVariantResponseDTO addVariant(Long productId, ProductVariantRequestDTO productVariantRequestDTO);

    ProductVariantResponseDTO updateVariant(Long variantId, UpdateVariantRequestDTO updateVariantRequestDTO);

    void updateStock(Long variantId, Integer quantity);

    void deleteVariant(Long variantId);

    void reserveStock(List<StockRequestDTO> reserveStockRequests);

    void releaseStock(List<StockRequestDTO> releaseStockRequests);
}
