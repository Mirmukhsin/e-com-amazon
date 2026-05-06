package org.ecomapp.productservice.services.productVariantService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.ProductVariantDTO;
import org.ecomapp.productservice.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productservice.dtos.request.StockRequestDTO;
import org.ecomapp.productservice.dtos.request.UpdateVariantRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductVariantResponseDTO;
import org.ecomapp.productservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.productservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productservice.models.Product;
import org.ecomapp.productservice.models.ProductVariant;
import org.ecomapp.productservice.repositories.ProductRepository;
import org.ecomapp.productservice.repositories.ProductVariantRepository;
import org.ecomapp.productservice.utility.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductVariantDTO> getAllByIds(Set<Long> variantIds) {
        return productVariantRepository.findByIdIn(variantIds).stream().map(productMapper::prodVariantDTOFromProdVariant).toList();
    }

    @Override
    public List<ProductVariantResponseDTO> getProductVariants(Long productId) {
        return productVariantRepository.findAllByProduct_Id(productId).stream().map(productMapper::prodVariantResDTOFromProdVariant).toList();
    }

    @Override
    public ProductVariantResponseDTO getVariant(Long variantId) {
        return productVariantRepository.findById(variantId)
                .map(productMapper::prodVariantResDTOFromProdVariant)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
    }

    @Override
    public ProductVariantResponseDTO addVariant(Long productId, ProductVariantRequestDTO productVariantRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductVariant productVariant = productMapper.prodVariantFromProdVariantReqDTO(productVariantRequestDTO);
        productVariant.setProduct(product);

        productVariantRepository.save(productVariant);

        return productMapper.prodVariantResDTOFromProdVariant(productVariant);
    }

    @Override
    public ProductVariantResponseDTO updateVariant(Long variantId, UpdateVariantRequestDTO updateVariantRequestDTO) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        if (updateVariantRequestDTO.getSku() != null && !updateVariantRequestDTO.getSku().isBlank()) {
            productVariant.setSku(updateVariantRequestDTO.getSku());
        }

        if (updateVariantRequestDTO.getPrice() != null && !updateVariantRequestDTO.getPrice().isNaN()) {
            productVariant.setPrice(updateVariantRequestDTO.getPrice());
        }

        if (updateVariantRequestDTO.getStockQuantity() != null) {
            productVariant.setStockQuantity(updateVariantRequestDTO.getStockQuantity());
        }

        if (updateVariantRequestDTO.getAttributes() != null && !updateVariantRequestDTO.getAttributes().isEmpty()) {
            productVariant.setAttributes(updateVariantRequestDTO.getAttributes());
        }

        productVariantRepository.save(productVariant);

        return productMapper.prodVariantResDTOFromProdVariant(productVariant);
    }

    @Override
    public void updateStock(Long variantId, Integer quantity) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        int newStock = productVariant.getStockQuantity() + quantity;

        if (newStock < 0) {
            throw new ConflictException("Insufficient stock");
        }

        productVariant.setStockQuantity(newStock);
        productVariantRepository.save(productVariant);
    }

    @Override
    public void deleteVariant(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
        productVariantRepository.delete(variant);
    }

    @Transactional
    @Override
    public void reserveStock(List<StockRequestDTO> requests) {
        for (StockRequestDTO req : requests) {
            ProductVariant variant = productVariantRepository.findById(req.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Variant not found: " + req.getVariantId()));

            int newStock = variant.getStockQuantity() - req.getQuantity();
            if (newStock < 0) {
                throw new ConflictException(
                        "Insufficient stock for: " + variant.getSku());
            }
            variant.setStockQuantity(newStock);
            productVariantRepository.save(variant);

        }
    }

    @Transactional
    @Override
    public void releaseStock(List<StockRequestDTO> requests) {
        for (StockRequestDTO req : requests) {
            ProductVariant variant = productVariantRepository.findById(req.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Variant not found: " + req.getVariantId()));

            variant.setStockQuantity(variant.getStockQuantity() + req.getQuantity());
            productVariantRepository.save(variant);
        }
    }


}
