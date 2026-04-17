package org.ecomapp.productservice.services.productService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.request.ProductRequestDTO;
import org.ecomapp.productservice.dtos.request.UpdateProductRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductResponseDTO;
import org.ecomapp.productservice.enums.ProductStatus;
import org.ecomapp.productservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productservice.models.Product;
import org.ecomapp.productservice.repositories.CategoryRepository;
import org.ecomapp.productservice.repositories.ProductRepository;
import org.ecomapp.productservice.utility.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(productMapper::productResDTOFromProduct);
    }

    @Override
    public Page<ProductResponseDTO> searchProducts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable).map(productMapper::productResDTOFromProduct);
    }

    @Override
    public Page<ProductResponseDTO> getProductsBySeller(int page, int size, Long sellerId) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllBySellerId(sellerId, pageable).map(productMapper::productResDTOFromProduct);
    }

    @Override
    public Page<ProductResponseDTO> getProductsByCategory(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByCategoryId(categoryId, pageable).map(productMapper::productResDTOFromProduct);
    }

    @Override
    public Page<ProductResponseDTO> getProductsByName(int page, int size, String productName) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByNameContainingIgnoreCase(productName, pageable).map(productMapper::productResDTOFromProduct);
    }

    @Override
    public ProductResponseDTO getProduct(Long productId) {
        return productRepository.findById(productId).map(productMapper::productResDTOFromProduct).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional
    @Override
    public ProductResponseDTO create(Long sellerId, ProductRequestDTO productRequestDTO) {
        Product product = productMapper.productFromProductReqDTO(productRequestDTO);

        product.setStatus(ProductStatus.ACTIVE);
        product.setSellerId(sellerId);

        productRepository.save(product);

        return productMapper.productResDTOFromProduct(product);
    }

    @Transactional
    @Override
    public ProductResponseDTO update(Long productId, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (updateProductRequestDTO.getName() != null)
            product.setName(updateProductRequestDTO.getName());
        if (updateProductRequestDTO.getDescription() != null)
            product.setDescription(updateProductRequestDTO.getDescription());
        if (updateProductRequestDTO.getBasePrice() != null)
            product.setBasePrice(updateProductRequestDTO.getBasePrice());
        if (updateProductRequestDTO.getCategoryId() != null) {
            Long categoryId = updateProductRequestDTO.getCategoryId();
            boolean existsById = categoryRepository.existsById(updateProductRequestDTO.getCategoryId());
            if (existsById) {
                product.setCategoryId(categoryId);
            }
        }

        productRepository.save(product);

        return productMapper.productResDTOFromProduct(product);
    }

    @Override
    public void changeProductStatus(Long productId, ProductStatus status) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStatus(status);
        productRepository.save(product);
    }
}
