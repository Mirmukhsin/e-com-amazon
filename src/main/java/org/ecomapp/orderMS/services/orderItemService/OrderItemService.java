package org.ecomapp.orderMS.services.orderItemService;

import org.ecomapp.orderMS.dtos.response.OrderItemResponseDTO;

import java.util.List;

public interface OrderItemService {

    List<OrderItemResponseDTO> getOrderItems(Long subOrderId);
}
