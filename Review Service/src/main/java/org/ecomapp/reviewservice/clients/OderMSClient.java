package org.ecomapp.reviewservice.clients;

import org.ecomapp.reviewservice.dtos.OrderItemDTOForReview;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORDER-SERVICE")
public interface OderMSClient {

    @GetMapping("/orders/sub-orders/items/{orderItemId}")
    OrderItemDTOForReview forReview(@PathVariable Long orderItemId);
}
