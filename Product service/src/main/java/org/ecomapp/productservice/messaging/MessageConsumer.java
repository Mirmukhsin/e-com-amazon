package org.ecomapp.productservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.request.ReserveStockRequestDTO;
import org.ecomapp.productservice.services.productVariantService.ProductVariantService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageConsumer {
    private final ProductVariantService productVariantService;

    @RabbitListener(queues = "Order-Product Queue")
    public void releaseStockMessage(List<ReserveStockRequestDTO> stockRequest) {
        productVariantService.releaseStock(stockRequest);
    }
}
