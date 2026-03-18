package org.ecomapp.reviewMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.reviewMS.dtos.request.ReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.request.UpdateReviewRequestDTO;
import org.ecomapp.reviewMS.dtos.response.ReviewResponseDTO;
import org.ecomapp.reviewMS.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "I. Reviews", description = "Product reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "Get all reviews")
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(reviewService.getAllReviews(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Get product reviews")
    @GetMapping("/products/{productId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getProductReviews(@PathVariable Long productId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(reviewService.getProductReviews(productId, page, size), HttpStatus.OK);
    }

//    @Operation(summary = "Get product reviews")
//    @GetMapping("/order-items/{orderItemId}")
//    public ResponseEntity<Page<ReviewResponseDTO>> getOrderItemReviews(@PathVariable Long orderItemId,
//                                                                       @RequestParam(defaultValue = "0") int page,
//                                                                       @RequestParam(defaultValue = "10") int size) {
//        return new ResponseEntity<>(reviewService.getOrderItemReviews(orderItemId, page, size), HttpStatus.OK);
//    }

    @Operation(summary = "Get review")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long reviewId) {
        return new ResponseEntity<>(reviewService.getReview(reviewId), HttpStatus.OK);
    }

    @Operation(summary = "Create review")
    @PostMapping("/products/{productId}/create/{orderItemId}")
    public ResponseEntity<ReviewResponseDTO> createReview(@PathVariable Long productId,
                                                          @PathVariable Long orderItemId,
                                                          @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        return new ResponseEntity<>(reviewService.createReview(productId, orderItemId, reviewRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update review")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long reviewId,
                                                          @Valid @RequestBody UpdateReviewRequestDTO updateReviewRequestDTO) {
        return new ResponseEntity<>(reviewService.updateReview(reviewId, updateReviewRequestDTO), HttpStatus.OK);
    }
}
