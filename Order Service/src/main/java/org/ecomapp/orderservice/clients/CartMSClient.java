package org.ecomapp.orderservice.clients;

import org.ecomapp.orderservice.dtos.clientsDTOs.CartItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "CART-SERVICE")
public interface CartMSClient {

    @GetMapping("/carts/items/by/{cartId}")
    List<CartItemDTO> getItemsByCartId(@PathVariable Long cartId);

    @DeleteMapping("/carts/{cartId}")
    void clearCart(@RequestHeader("X-User-Id") Long userId, @PathVariable Long cartId);
}
