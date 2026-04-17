package org.ecomapp.orderservice.repositories;

import org.ecomapp.orderservice.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllBySubOrder_Id(Long subOrderId);

    List<OrderItem> findAllBySubOrderIdIn(Collection<Long> subOrderIds);
}
