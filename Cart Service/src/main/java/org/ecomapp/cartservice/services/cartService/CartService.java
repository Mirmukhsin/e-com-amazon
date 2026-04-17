package org.ecomapp.cartservice.services.cartService;

import org.ecomapp.cartservice.dtos.response.CartResponseDTO;

public interface CartService {

    CartResponseDTO getCart(Long cartId);

    CartResponseDTO createCart(Long userId);

    void deleteCart(Long userId,Long cartId);
}
