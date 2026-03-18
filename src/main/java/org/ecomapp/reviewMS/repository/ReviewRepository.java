package org.ecomapp.reviewMS.repository;

import org.ecomapp.reviewMS.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByProduct_Id(Long productId, Pageable pageable);

    Page<Review> findAllByOrderItem_Id(Long orderItemId, Pageable pageable);

    boolean existsByOrderItem_IdAndBuyer_Id(Long orderItemId, Long buyerId);
}
