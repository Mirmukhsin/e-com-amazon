package org.ecomapp.paymentservice.clients;

import org.ecomapp.paymentservice.dtos.clientDTOs.OderDTOForPayment;
import org.ecomapp.paymentservice.dtos.clientDTOs.SubOderDTOForPayment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderMSClient {

    @GetMapping("/orders/forPayment/{orderId}")
    OderDTOForPayment getOrder(@PathVariable Long orderId);

    @GetMapping("/orders/sub-orders/forPayment/{subOrderId}")
    SubOderDTOForPayment getSubOrder(@PathVariable Long subOrderId);
}
