package org.ecomapp.orderMS.services.subOrderService;

import org.ecomapp.orderMS.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderMS.enums.SubOrderStatus;
import org.springframework.data.domain.Page;

public interface SubOrderService {

    SubOrderResponseDTO getSubOrder(Long subOrderId);

    Page<SubOrderResponseDTO> getSellerSubOrders(int page, int size);

    SubOrderResponseDTO updateSubOrderStatus(Long subOrderId, SubOrderStatus subOrderStatus);

}
