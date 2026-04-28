package org.ecomapp.paymentservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void changeOrderStatusMessage(ChangeOrderStatusDTO dto) {
        rabbitTemplate.convertAndSend("Payment-Order Queue", dto);
    }
}
