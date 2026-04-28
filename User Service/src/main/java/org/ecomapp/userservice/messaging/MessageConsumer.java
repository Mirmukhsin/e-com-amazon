package org.ecomapp.userservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.services.sellerService.SellerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageConsumer {
    private final SellerService sellerService;

    @RabbitListener(queues = "Order-User Queue")
    public void updateTotalSalesMessage(UpdateTotalSalesDTO dto) {
        sellerService.updateTotalSales(dto.sellerId(), dto.totalSales());
    }
}
