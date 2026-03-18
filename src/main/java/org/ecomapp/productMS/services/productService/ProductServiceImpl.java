package org.ecomapp.productMS.services.productService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productMS.dtos.request.ProductRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductResponseDTO;
import org.ecomapp.productMS.enums.ProductStatus;
import org.ecomapp.productMS.models.Category;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.repositories.CategoryRepository;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.productMS.utility.ProductMapper;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(productMapper::productToProductResDto);
    }

    @Override
    public Page<ProductResponseDTO> searchProducts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable).map(productMapper::productToProductResDto);
    }

    @Override
    public Page<ProductResponseDTO> getProductsBySeller(int page, int size, Long sellerId) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllBySeller_Id(sellerId, pageable).map(productMapper::productToProductResDto);
    }

    // TODO: should get product by parent category id
    @Override
    public Page<ProductResponseDTO> getProductsByCategory(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByCategory_Id(categoryId, pageable).map(productMapper::productToProductResDto);
    }

    @Override
    public Page<ProductResponseDTO> getProductsByName(int page, int size, String productName) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllByNameContainingIgnoreCase(productName, pageable).map(productMapper::productToProductResDto);
    }

    @Override
    public ProductResponseDTO getProduct(Long productId) {
        return productRepository.findById(productId).map(productMapper::productToProductResDto).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Transactional
    @Override
    // TODO: SellerId in the dto should removed. it will came from JWT token
    public ProductResponseDTO create(ProductRequestDTO productRequestDTO) {

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        User seller = userRepository.findById(productRequestDTO.getSellerId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = productMapper.productReqDtoToProduct(productRequestDTO);

        product.setStatus(ProductStatus.ACTIVE);
        product.setCategory(category);
        product.setSeller(seller);

        productRepository.save(product);

        return productMapper.productToProductResDto(product);
    }

    @Transactional
    @Override
    public ProductResponseDTO update(Long productId, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (productRequestDTO.getName() != null) product.setName(productRequestDTO.getName());
        if (productRequestDTO.getDescription() != null) product.setDescription(productRequestDTO.getDescription());
        if (productRequestDTO.getBasePrice() != null) product.setBasePrice(productRequestDTO.getBasePrice());
        if (productRequestDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        productRepository.save(product);

        return productMapper.productToProductResDto(product);
    }

    @Override
    public void changeProductStatus(Long productId, ProductStatus status) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStatus(status);
        productRepository.save(product);
    }
}
