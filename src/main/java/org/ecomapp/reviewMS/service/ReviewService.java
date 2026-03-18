package org.ecomapp.reviewMS.service;

import org.ecomapp.reviewMS.dtos.request.ReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.request.UpdateReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.response.ReviewResponseDTO;
import org.springframework.data.domain.Page;

public interface ReviewService {

    Page<ReviewResponseDTO> getAllReviews(int page, int size);

    Page<ReviewResponseDTO> getProductReviews(Long productId, int page, int size);

    Page<ReviewResponseDTO> getOrderItemReviews(Long orderItemId, int page, int size);

    ReviewResponseDTO getReview(Long reviewId);

    ReviewResponseDTO createReview(Long productId, Long orderItemId, ReviewRequestDTO reviewRequestDTO);

    ReviewResponseDTO updateReview(Long reviewId, UpdateReviewRequestDTO updateReviewRequestDTO);
}
