package org.ecomapp.paymentMS.repositories;

import org.ecomapp.paymentMS.models.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    boolean existsBySubOrder_Id(Long subOrderId);
}
