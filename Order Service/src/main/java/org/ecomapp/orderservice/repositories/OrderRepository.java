package org.ecomapp.orderservice.repositories;

import org.ecomapp.orderservice.enums.OrderStatus;
import org.ecomapp.orderservice.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByBuyerId(Long buyerId, Pageable pageable);

    Page<Order> findAllByBuyerIdAndStatus(Long buyerId, OrderStatus orderStatus, Pageable pageable);
}
