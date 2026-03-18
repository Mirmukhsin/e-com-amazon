package org.ecomapp.productMS.services.productVariantService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productMS.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductVariantResponseDTO;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.productMS.repositories.ProductVariantRepository;
import org.ecomapp.productMS.utility.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductVariantResponseDTO> getProductVariants(Long productId) {
        return productVariantRepository.findAllByProduct_Id(productId).stream().map(productMapper::prodVariantToProdVariantResDto).toList();
    }

    @Override
    public ProductVariantResponseDTO getVariant(Long variantId) {
        return productVariantRepository.findById(variantId)
                .map(productMapper::prodVariantToProdVariantResDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
    }

    @Override
    public ProductVariantResponseDTO addVariant(Long productId, ProductVariantRequestDTO productVariantRequestDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductVariant productVariant = productMapper.prodVariantReqDtoToProdVariant(productVariantRequestDTO);
        productVariant.setProduct(product);

        productVariantRepository.save(productVariant);

        return productMapper.prodVariantToProdVariantResDto(productVariant);
    }

    @Override
    public ProductVariantResponseDTO updateVariant(Long variantId, ProductVariantRequestDTO productVariantRequestDTO) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        productVariant.setSku(productVariantRequestDTO.getSku());
        productVariant.setPrice(productVariantRequestDTO.getPrice());
        productVariant.setStockQuantity(productVariantRequestDTO.getStockQuantity());
        productVariant.setAttributes(productVariantRequestDTO.getAttributes());

        productVariantRepository.save(productVariant);

        return productMapper.prodVariantToProdVariantResDto(productVariant);
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
}
