package org.ecomapp.orderservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.StockRequest;
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

    public void reserveStockMessage(List<StockRequest> stockRequests) {
        rabbitTemplate.convertAndSend("Order-Product-reserve Queue", stockRequests);
    }

    public void releaseStockMessage(List<StockRequest> stockRequests) {
        rabbitTemplate.convertAndSend("Order-Product-release Queue", stockRequests);
    }

    public void updateTotalSalesMessage(UpdateTotalSalesDTO dto) {
        rabbitTemplate.convertAndSend("Order-User Queue", dto);
    }

}
