package org.ecomapp.orderMS.services.orderService;

import org.ecomapp.orderMS.dtos.request.CheckoutRequestDTO;
import org.ecomapp.orderMS.dtos.response.OrderResponseDTO;
import org.ecomapp.orderMS.enums.OrderStatus;
import org.springframework.data.domain.Page;

public interface OrderService {

    Page<OrderResponseDTO> getUserOrders(OrderStatus orderStatus, int page, int size);

    OrderResponseDTO getOrder(Long orderId);

    OrderResponseDTO createOrder(CheckoutRequestDTO checkoutRequestDTO);

    void cancelOrder(Long orderId);
}
