package org.ecomapp.orderservice.tolerance;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.clients.CartMSClient;
import org.ecomapp.orderservice.dtos.clientsDTOs.CartItemDTO;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartGateway {
    private final CartMSClient cartMSClient;

    @CircuitBreaker(name = "cartService", fallbackMethod = "getItemsFallBack")
    @Retry(name = "cartService")
    @RateLimiter(name = "cartService")
    public List<CartItemDTO> getItemsByCartId(Long cartId) {
        return cartMSClient.getItemsByCartId(cartId);
    }

    public List<CartItemDTO> getItemsFallBack(Long cartId, Exception e) {
        throw new ServiceUnavailableException("Service temporarily unavailable, please try again.");
    }
}
