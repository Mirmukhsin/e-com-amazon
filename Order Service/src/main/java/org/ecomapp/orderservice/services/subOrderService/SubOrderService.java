package org.ecomapp.orderservice.services.subOrderService;

import org.ecomapp.orderservice.dtos.clientsDTOs.SubOderDTOForPayment;
import org.ecomapp.orderservice.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderservice.enums.SubOrderStatus;
import org.springframework.data.domain.Page;

public interface SubOrderService {

    SubOrderResponseDTO getSubOrder(Long subOrderId);

    Page<SubOrderResponseDTO> getSellerSubOrders(Long sellerId, int page, int size);

    SubOrderResponseDTO updateSubOrderStatus(Long sellerId, Long subOrderId, SubOrderStatus subOrderStatus);

    SubOderDTOForPayment getSubOrderForPayment(Long subOrderId);
}
