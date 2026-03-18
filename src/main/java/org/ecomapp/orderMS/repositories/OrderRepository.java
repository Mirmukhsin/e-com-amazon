package org.ecomapp.orderMS.repositories;

import org.ecomapp.orderMS.enums.OrderStatus;
import org.ecomapp.orderMS.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByBuyer_Id(Long buyerId, Pageable pageable);

    List<Order> findAllByBuyer_Id(Long buyerId);

    Page<Order> findAllByBuyer_IdAndStatus(Long buyerId, OrderStatus orderStatus, Pageable pageable);
}
