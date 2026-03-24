package org.ecomapp.reviewMSTest;

import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderMS.enums.SubOrderStatus;
import org.ecomapp.orderMS.models.Order;
import org.ecomapp.orderMS.models.OrderItem;
import org.ecomapp.orderMS.models.SubOrder;
import org.ecomapp.orderMS.repositories.OrderItemRepository;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.reviewMS.dtos.request.ReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.request.UpdateReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.response.ReviewResponseDTO;
import org.ecomapp.reviewMS.models.Review;
import org.ecomapp.reviewMS.repository.ReviewRepository;
import org.ecomapp.reviewMS.service.ReviewServiceImpl;
import org.ecomapp.reviewMS.utility.ReviewMapper;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.SellerProfile;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.SellerRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User buyer;
    private User seller;
    private Product product;
    private OrderItem orderItem;
    private Review review;
    private ReviewResponseDTO reviewResponseDTO;

    @BeforeEach
    void setUp() {
        buyer = User.builder().id(1L).email("buyer@gmail.com").build();
        seller = User.builder().id(2L).email("seller@gmail.com").build();

        product = Product.builder()
                .id(1L)
                .name("ROG Laptop")
                .seller(seller)
                .build();

        SubOrder subOrder = SubOrder.builder()
                .id(1L)
                .status(SubOrderStatus.DELIVERED)
                .order(Order.builder()
                        .id(1L)
                        .buyer(buyer)
                        .build())
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .subOrder(subOrder)
                .productVariant(
                        ProductVariant.builder()
                                .id(1L)
                                .product(product)
                                .build())
                .build();

        review = Review.builder()
                .id(1L)
                .buyer(buyer)
                .product(product)
                .orderItem(orderItem)
                .rating(5)
                .comment("Amazing product!")
                .build();

        reviewResponseDTO = ReviewResponseDTO.builder()
                .id(1L)
                .buyerId(1L)
                .productId(1L)
                .rating(5)
                .comment("Amazing product!")
                .build();
    }

    // ─────────────────────────────────────────
    // CREATE REVIEW TESTS
    // ─────────────────────────────────────────

    @Test
    void createReview_shouldCreateReview_whenValid() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setRating(5);
        dto.setComment("Amazing product!");
        dto.setOrderItemId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(reviewRepository.existsByOrderItem_IdAndBuyer_Id(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.reviewToReviewResDto(any(Review.class))).thenReturn(reviewResponseDTO);
        when(sellerRepository.findByUserId(seller.getId())).thenReturn(Optional.of(new SellerProfile()));

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        ReviewResponseDTO result = reviewService.createReview(1L, 1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_shouldThrowException_whenProductNotFound() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setOrderItemId(1L);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(99L, 1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void createReview_shouldThrowException_whenAlreadyReviewed() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setOrderItemId(1L);

        when(reviewRepository.existsByOrderItem_IdAndBuyer_Id(1L, 1L)).thenReturn(true);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already reviewed");

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_shouldThrowException_whenOrderItemNotBelongToProduct() {
        ReviewRequestDTO dto = new ReviewRequestDTO();
        dto.setOrderItemId(1L);

        Product anotherProduct = Product.builder().id(99L).name("Another Product").build();
        orderItem.getProductVariant().setProduct(anotherProduct);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(reviewRepository.existsByOrderItem_IdAndBuyer_Id(1L, 1L)).thenReturn(false);
        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, dto))
                .isInstanceOf(ConflictException.class);
    }

    // ─────────────────────────────────────────
    // UPDATE REVIEW TESTS
    // ─────────────────────────────────────────

    @Test
    void updateReview_shouldUpdateReview_whenOwner() {
        UpdateReviewRequestDTO dto = new UpdateReviewRequestDTO();
        dto.setRating(4);
        dto.setComment("Good product");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.reviewToReviewResDto(review)).thenReturn(reviewResponseDTO);
        when(sellerRepository.findByUserId(seller.getId())).thenReturn(Optional.of(new SellerProfile()));

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        ReviewResponseDTO result = reviewService.updateReview(1L, dto);

        assertThat(result).isNotNull();
        verify(reviewRepository).save(review);
    }

    @Test
    void updateReview_shouldThrowException_whenNotOwner() {
        UpdateReviewRequestDTO dto = new UpdateReviewRequestDTO();
        dto.setRating(4);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(1L, dto))
                .isInstanceOf(UnAuthorizedException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReview_shouldThrowException_whenReviewNotFound() {
        UpdateReviewRequestDTO dto = new UpdateReviewRequestDTO();

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    // ─────────────────────────────────────────
    // GET REVIEW TESTS
    // ─────────────────────────────────────────

    @Test
    void getReview_shouldReturnReview_whenFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.reviewToReviewResDto(review)).thenReturn(reviewResponseDTO);

        ReviewResponseDTO result = reviewService.getReview(1L);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
    }

    @Test
    void getReview_shouldThrowException_whenNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReview(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
