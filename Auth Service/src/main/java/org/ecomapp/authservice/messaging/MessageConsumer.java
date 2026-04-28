package org.ecomapp.authservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.authservice.services.AuthService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageConsumer {
    private final AuthService authService;

    @RabbitListener(queues = "User Deleting Queue")
    public void consumeDeleteUserMessage(Long userId){
        authService.disableUser(userId);
    }
}
