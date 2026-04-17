package org.ecomapp.paymentservice.repositories;

import org.ecomapp.paymentservice.enums.PaymentStatus;
import org.ecomapp.paymentservice.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Page<Payment> findAllByBuyerId(Long buyerId, Pageable pageable);

    Optional<Payment> findByOrderId(Long orderId);

    boolean existsByOrderIdAndStatusIn(Long orderId, List<PaymentStatus> statuses);
}
