package org.ecomapp.orderservice.services.orderItemService;

import org.ecomapp.orderservice.dtos.clientsDTOs.OrderItemDTOForReview;
import org.ecomapp.orderservice.dtos.response.OrderItemResponseDTO;

import java.util.List;

public interface OrderItemService {

    List<OrderItemResponseDTO> getOrderItems(Long subOrderId);

    OrderItemDTOForReview forReview(Long orderItemId);
}
