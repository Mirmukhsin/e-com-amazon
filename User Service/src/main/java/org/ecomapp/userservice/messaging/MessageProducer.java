package org.ecomapp.userservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void deleteUserMessage(Long userId) {
        rabbitTemplate.convertAndSend("User Deleting Queue", userId);
    }
}
