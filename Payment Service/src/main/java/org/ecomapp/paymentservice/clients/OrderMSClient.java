package org.ecomapp.paymentservice.clients;

import org.ecomapp.paymentservice.dtos.clientDTOs.OderDTOForPayment;
import org.ecomapp.paymentservice.dtos.clientDTOs.SubOderDTOForPayment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderMSClient {

    @GetMapping("/orders/forPayment/{orderId}")
    OderDTOForPayment getOrder(@PathVariable Long orderId);

    @GetMapping("/orders/sub-orders/forPayment/{subOrderId}")
    SubOderDTOForPayment getSubOrder(@PathVariable Long subOrderId);

    @PatchMapping("/orders/{orderId}/status")
    void changeStatus(@PathVariable Long orderId, @RequestBody String orderStatus);
}
