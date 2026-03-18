package org.ecomapp.cartMS.services.cartService;

import org.ecomapp.cartMS.dtos.response.CartResponseDTO;

public interface CartService {

    CartResponseDTO getCart(Long cartId);

    CartResponseDTO createCart();

    void deleteCart(Long cartId);
}
