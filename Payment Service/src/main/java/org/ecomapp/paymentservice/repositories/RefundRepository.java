package org.ecomapp.paymentservice.repositories;

import org.ecomapp.paymentservice.models.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    boolean existsBySubOrderId(Long subOrderId);
}
