package org.ecomapp.paymentservice.tolerance;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.ecomapp.paymentservice.clients.OrderMSClient;
import org.ecomapp.paymentservice.dtos.clientDTOs.OderDTOForPayment;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.ServiceUnavailableException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderGateway {
    private final OrderMSClient orderMSClient;

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    @Retry(name = "orderService")
    @RateLimiter(name = "orderService")
    public OderDTOForPayment getOrder(Long orderId) {
        return orderMSClient.getOrder(orderId);
    }

    public OderDTOForPayment getOrderFallback(Long orderId, Exception e) {
        throw new ServiceUnavailableException("Service temporarily unavailable, please try again.");
    }
}
