package org.ecomapp.orderservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.ReserveStockRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void clearCartMessage(ClearCartDTO dto) {
        rabbitTemplate.convertAndSend("Order-Cart Queue", dto);
    }

    public void releaseStockMessage(List<ReserveStockRequest> stockRequests) {
        rabbitTemplate.convertAndSend("Order-Product Queue", stockRequests);
    }

    public void updateTotalSalesMessage(UpdateTotalSalesDTO dto) {
        rabbitTemplate.convertAndSend("Order-User Queue", dto);
    }

}
