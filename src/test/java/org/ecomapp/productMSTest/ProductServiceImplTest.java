package org.ecomapp.productMSTest;

import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productMS.dtos.request.ProductRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductResponseDTO;
import org.ecomapp.productMS.enums.ProductStatus;
import org.ecomapp.productMS.models.Category;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.repositories.CategoryRepository;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.productMS.services.productService.ProductServiceImpl;
import org.ecomapp.productMS.utility.ProductMapper;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ProductServiceImpl productService;

    private User seller;
    private Category category;
    private Product product;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .id(1L)
                .email("seller@gmail.com")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Gaming Laptops")
                .build();

        product = Product.builder()
                .id(1L)
                .name("ROG STRIX G17")
                .description("Gaming Laptop")
                .basePrice(1200.0)
                .status(ProductStatus.ACTIVE)
                .seller(seller)
                .category(category)
                .build();

        productResponseDTO = ProductResponseDTO.builder()
                .id(1L)
                .name("ROG STRIX G17")
                .basePrice(1200.0)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    // -----------------------------------------
    // GET PRODUCT TESTS
    // -----------------------------------------

    @Test
    void getProduct_shouldReturnProduct_whenFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductResDto(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.getProduct(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ROG STRIX G17");
    }

    @Test
    void getProduct_shouldThrowException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    // -----------------------------------------
    // GET ALL PRODUCTS TESTS
    // -----------------------------------------

    @Test
    void getAllProducts_shouldReturnPageOfProducts() {
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.productToProductResDto(product)).thenReturn(productResponseDTO);

        Page<ProductResponseDTO> result = productService.getAllProducts(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAllProducts_shouldReturnEmptyPage_whenNoProducts() {
        Page<Product> emptyPage = new PageImpl<>(List.of());

        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<ProductResponseDTO> result = productService.getAllProducts(0, 10);

        assertThat(result.getContent()).isEmpty();
    }

    // -----------------------------------------
    // CREATE PRODUCT TESTS
    // -----------------------------------------

    @Test
    void createProduct_shouldReturnProduct_whenValid() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("ROG STRIX G17");
        dto.setDescription("Gaming Laptop");
        dto.setBasePrice(1200.0);
        dto.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(productMapper.productReqDtoToProduct(dto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.productToProductResDto(product)).thenReturn(productResponseDTO);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        ProductResponseDTO result = productService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ROG STRIX G17");
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_shouldThrowException_whenCategoryNotFound() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setCategoryId(99L);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createProduct_shouldThrowException_whenSellerNotFound() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -----------------------------------------
    // UPDATE PRODUCT TESTS
    // -----------------------------------------

    @Test
    void updateProduct_shouldUpdateProduct_whenValid() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Updated ROG");
        dto.setBasePrice(1300.0);
        dto.setCategoryId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.productToProductResDto(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.update(1L, dto);

        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {
        ProductRequestDTO dto = new ProductRequestDTO();

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    // -----------------------------------------
    // CHANGE STATUS TESTS
    // -----------------------------------------

    @Test
    void changeProductStatus_shouldUpdateStatus_whenProductFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.changeProductStatus(1L, ProductStatus.INACTIVE);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        verify(productRepository).save(product);
    }

    @Test
    void changeProductStatus_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.changeProductStatus(99L, ProductStatus.INACTIVE))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

