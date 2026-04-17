package org.ecomapp.reviewservice.repository;

import org.ecomapp.reviewservice.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByProductId(Long productId, Pageable pageable);

    Page<Review> findAllByOrderItemId(Long orderItemId, Pageable pageable);

    boolean existsByOrderItemIdAndBuyerId(Long orderItemId, Long buyerId);
}
