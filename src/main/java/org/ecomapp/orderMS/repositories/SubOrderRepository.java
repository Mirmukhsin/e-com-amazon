package org.ecomapp.orderMS.repositories;

import org.ecomapp.orderMS.models.SubOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SubOrderRepository extends JpaRepository<SubOrder, Long> {

    List<SubOrder> findAllByOrder_Id(Long orderId);

    Page<SubOrder> findAllBySeller_Id(Long sellerId, Pageable pageable);

    List<SubOrder> findAllByOrderIdIn(Collection<Long> orderIds);
}
