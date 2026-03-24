package org.ecomapp.reviewMS.service;

import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderMS.models.OrderItem;
import org.ecomapp.orderMS.repositories.OrderItemRepository;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.reviewMS.dtos.request.ReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.request.UpdateReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.response.ReviewResponseDTO;
import org.ecomapp.reviewMS.models.Review;
import org.ecomapp.reviewMS.repository.ReviewRepository;
import org.ecomapp.reviewMS.utility.ReviewMapper;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.SellerProfile;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.SellerRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final OrderItemRepository orderItemRepository;
    private final SellerRepository sellerRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Page<ReviewResponseDTO> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAll(pageable).map(reviewMapper::reviewToReviewResDto);
    }

    @Override
    public Page<ReviewResponseDTO> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByProduct_Id(productId, pageable).map(reviewMapper::reviewToReviewResDto);
    }

    @Override
    public Page<ReviewResponseDTO> getOrderItemReviews(Long orderItemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByOrderItem_Id(orderItemId, pageable).map(reviewMapper::reviewToReviewResDto);
    }

    @Override
    public ReviewResponseDTO getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).map(reviewMapper::reviewToReviewResDto).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    @Override
    public ReviewResponseDTO createReview(Long productId, Long orderItemId, ReviewRequestDTO reviewRequestDTO) {
        Long currentUserId = securityUtils.getCurrentUserId();

        boolean alreadyReviewed = reviewRepository.existsByOrderItem_IdAndBuyer_Id(orderItemId, currentUserId);

        if (alreadyReviewed) {
            throw new ConflictException("You have already reviewed this item");
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        User buyer = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getSubOrder().getOrder().getBuyer().getId().equals(currentUserId)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        if (!orderItem.getProductVariant().getProduct().getId().equals(productId)) {
            throw new ConflictException("Order item does not belong to this product");
        }

        Review review = Review.builder()
                .buyer(buyer)
                .product(product)
                .orderItem(orderItem)
                .rating(reviewRequestDTO.getRating())
                .comment(reviewRequestDTO.getComment())
                .build();
        reviewRepository.save(review);

        updateSellerAverageRating(product);

        return reviewMapper.reviewToReviewResDto(review);
    }

    @Override
    public ReviewResponseDTO updateReview(Long reviewId, UpdateReviewRequestDTO updateReviewRequestDTO) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getBuyer().getId().equals(securityUtils.getCurrentUserId())) {
            throw new UnAuthorizedException("Unauthorized");
        }

        review.setRating(updateReviewRequestDTO.getRating());
        review.setComment(updateReviewRequestDTO.getComment());
        reviewRepository.save(review);

        updateSellerAverageRating(review.getProduct());

        return reviewMapper.reviewToReviewResDto(review);
    }

    private void updateSellerAverageRating(Product product) {
        User seller = product.getSeller();

        List<Review> allSellerReviews = reviewRepository.findAllByProduct_Seller_Id(seller.getId());

        double averageRating = allSellerReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        averageRating = Math.round(averageRating * 10.0) / 10.0;

        SellerProfile sellerProfile = sellerRepository.findByUserId(seller.getId()).orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        sellerProfile.setAverageRating(averageRating);
        sellerRepository.save(sellerProfile);
    }
}
