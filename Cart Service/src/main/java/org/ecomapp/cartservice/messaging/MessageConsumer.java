package org.ecomapp.cartservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.cartservice.services.cartService.CartService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageConsumer {
    private final CartService cartService;

    @RabbitListener(queues = "Order-Cart Queue")
    public void clearCartMessage(ClearCartDTO dto) {
        cartService.deleteCart(dto.userId(), dto.cartId());
    }
}
