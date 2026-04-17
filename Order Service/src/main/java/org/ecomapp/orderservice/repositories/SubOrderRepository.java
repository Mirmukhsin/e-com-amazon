package org.ecomapp.orderservice.repositories;

import org.ecomapp.orderservice.models.SubOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SubOrderRepository extends JpaRepository<SubOrder, Long> {

    List<SubOrder> findAllByOrderId(Long orderId);

    Page<SubOrder> findAllBySellerId(Long sellerId, Pageable pageable);

    List<SubOrder> findAllByOrderIdIn(Collection<Long> orderIds);
}
