package org.ecomapp.reviewservice.service;

import lombok.RequiredArgsConstructor;
import org.ecomapp.reviewservice.clients.OderMSClient;
import org.ecomapp.reviewservice.dtos.OrderItemDTOForReview;
import org.ecomapp.reviewservice.dtos.request.ReviewRequestDTO;
import org.ecomapp.reviewservice.dtos.request.UpdateReviewRequestDTO;
import org.ecomapp.reviewservice.dtos.response.ReviewResponseDTO;
import org.ecomapp.reviewservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.reviewservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.reviewservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.reviewservice.models.Review;
import org.ecomapp.reviewservice.repository.ReviewRepository;
import org.ecomapp.reviewservice.utility.ReviewMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OderMSClient oderMSClient;

    @Override
    public Page<ReviewResponseDTO> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAll(pageable).map(reviewMapper::reviewToReviewResDto);
    }

    @Override
    public Page<ReviewResponseDTO> getProductReviews(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByProductId(productId, pageable).map(reviewMapper::reviewToReviewResDto);
    }

    @Override
    public ReviewResponseDTO getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).map(reviewMapper::reviewToReviewResDto).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    @Override
    public ReviewResponseDTO createReview(Long userId, Long productId, Long orderItemId, ReviewRequestDTO reviewRequestDTO) {

        boolean alreadyReviewed = reviewRepository.existsByOrderItemIdAndBuyerId(orderItemId, userId);

        if (alreadyReviewed) {
            throw new ConflictException("You have already reviewed this item");
        }

        OrderItemDTOForReview orderItem = oderMSClient.forReview(orderItemId);

        if (!orderItem.getBuyerId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        Review review = Review.builder()
                .buyerId(userId)
                .productId(productId)
                .orderItemId(orderItemId)
                .rating(reviewRequestDTO.getRating())
                .comment(reviewRequestDTO.getComment())
                .build();
        reviewRepository.save(review);

//        TODO: seller service issue
//        updateSellerAverageRating(productId);

        return reviewMapper.reviewToReviewResDto(review);
    }

    @Override
    public ReviewResponseDTO updateReview(Long userId, Long reviewId, UpdateReviewRequestDTO updateReviewRequestDTO) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getBuyerId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        review.setRating(updateReviewRequestDTO.getRating());
        review.setComment(updateReviewRequestDTO.getComment());
        reviewRepository.save(review);

//        TODO: seller service issue
//        updateSellerAverageRating(review.getProductId());

        return reviewMapper.reviewToReviewResDto(review);
    }

//    private void updateSellerAverageRating(Long productId) {
//        User seller = product.getSeller();
//
//        List<Review> allSellerReviews = reviewRepository.findAllByProduct_Seller_Id(seller.getId());
//
//        double averageRating = allSellerReviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
//
//        averageRating = Math.round(averageRating * 10.0) / 10.0;
//
//        SellerProfile sellerProfile = sellerRepository.findByUserId(seller.getId()).orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));
//
//        sellerProfile.setAverageRating(averageRating);
//        sellerRepository.save(sellerProfile);
//    }
}
