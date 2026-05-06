package org.ecomapp.productservice.messaging;

import lombok.RequiredArgsConstructor;
import org.ecomapp.productservice.dtos.request.StockRequestDTO;
import org.ecomapp.productservice.services.productVariantService.ProductVariantService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageConsumer {
    private final ProductVariantService productVariantService;

    @RabbitListener(queues = "Order-Product-release Queue")
    public void releaseStockMessage(List<StockRequestDTO> stockRequest) {
        productVariantService.releaseStock(stockRequest);
    }

    @RabbitListener(queues = "Order-Product-reserve Queue")
    public void reserveStockMessage(List<StockRequestDTO> stockRequest) {
        productVariantService.reserveStock(stockRequest);
    }
}
