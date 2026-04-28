package org.ecomapp.orderservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.services.orderService.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageConsumer {
    private final OrderService orderService;

    @RabbitListener(queues = "Payment-Order Queue")
    public void changeOrderStatusMessage(ChangeOrderStatusDTO dto) {
        orderService.changeStatus(dto.orderId(), dto.orderStatus());
    }
}
