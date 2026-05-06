package org.ecomapp.orderservice.tolerance;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.clients.ProductMSClient;
import org.ecomapp.orderservice.dtos.clientsDTOs.ProductVariantDTO;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductGateway {
    private final ProductMSClient productMSClient;

    @CircuitBreaker(name = "productService", fallbackMethod = "getAllFallBack")
    @Retry(name = "productService")
    @RateLimiter(name = "productService")
    public List<ProductVariantDTO> getAllByIds(Set<Long> variantIds) {
        return productMSClient.getAllByIds(variantIds);
    }

    public List<ProductVariantDTO> getAllFallBack(Set<Long> variantIds, Exception e) {
        throw new ServiceUnavailableException("Service temporarily unavailable, please try again.");
    }
}
