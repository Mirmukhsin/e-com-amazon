package org.ecomapp.orderservice.tolerance;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.clients.UserMSClient;
import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressDTO;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ServiceUnavailableException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGateway {
    private final UserMSClient userMSClient;

    @CircuitBreaker(name = "userService", fallbackMethod = "getByIdFallBack")
    @Retry(name = "userService")
    @RateLimiter(name = "userService")
    public AddressDTO getById(Long userId, Long addressId) {
        return userMSClient.getById(userId, addressId);
    }

    public AddressDTO getByIdFallBack(Long userId, Long addressId, Exception e) {
        throw new ServiceUnavailableException("Service temporarily unavailable, please try again.");
    }
}
