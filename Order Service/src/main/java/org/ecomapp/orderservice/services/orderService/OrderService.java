package org.ecomapp.orderservice.services.orderService;

import org.ecomapp.orderservice.dtos.clientsDTOs.OderDTOForPayment;
import org.ecomapp.orderservice.dtos.request.CheckoutRequestDTO;
import org.ecomapp.orderservice.dtos.response.OrderResponseDTO;
import org.ecomapp.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;

public interface OrderService {

    Page<OrderResponseDTO> getUserOrders(Long userId, OrderStatus orderStatus, int page, int size);

    OrderResponseDTO getOrder(Long userId, Long orderId);

    OrderResponseDTO createOrder(Long userId, CheckoutRequestDTO checkoutRequestDTO);

    void cancelOrder(Long userId, Long orderId);

//  TODO:   for payment

    OderDTOForPayment getOrderForPayment(Long orderId);

    void changeStatus(Long orderId, String orderStatus);
}
