package org.ecomapp.cartMS.services.cartItemService;

import org.ecomapp.cartMS.dtos.request.AddCartItemRequestDTO;
import org.ecomapp.cartMS.dtos.request.UpdateCartItemRequestDTO;
import org.ecomapp.cartMS.dtos.response.CartItemResponseDTO;

public interface CartItemService {

    CartItemResponseDTO getCartItem(Long cartItemId);

    CartItemResponseDTO addCartItem(Long cartId, Long productVariantId, AddCartItemRequestDTO addCartItemRequestDTO);

    CartItemResponseDTO updateCartItem(Long cartItemId, UpdateCartItemRequestDTO updateCartItemRequestDTO);

    void deleteCartItem(Long cartItemId);
}
