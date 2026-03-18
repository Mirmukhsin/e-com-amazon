package org.ecomapp.paymentMS.repositories;

import org.ecomapp.paymentMS.enums.PaymentStatus;
import org.ecomapp.paymentMS.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Page<Payment> findAllByBuyer_Id(Long buyerId, Pageable pageable);

    Optional<Payment> findByOrder_Id(Long orderId);

    boolean existsByOrder_IdAndStatusIn(Long orderId, List<PaymentStatus> statuses);
}
