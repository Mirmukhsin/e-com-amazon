package org.ecomapp.reviewservice.service;

import org.ecomapp.reviewservice.dtos.request.ReviewRequestDTO;
import org.ecomapp.reviewservice.dtos.request.UpdateReviewRequestDTO;
import org.ecomapp.reviewservice.dtos.response.ReviewResponseDTO;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Page<ReviewResponseDTO> getAllReviews(int page, int size);

    Page<ReviewResponseDTO> getProductReviews(Long productId, int page, int size);

    ReviewResponseDTO getReview(Long reviewId);

    ReviewResponseDTO createReview(Long userId,Long productId, Long orderItemId, ReviewRequestDTO reviewRequestDTO);

    ReviewResponseDTO updateReview(Long userId,Long reviewId, UpdateReviewRequestDTO updateReviewRequestDTO);
}
