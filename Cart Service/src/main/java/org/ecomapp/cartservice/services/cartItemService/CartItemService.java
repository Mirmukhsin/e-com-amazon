package org.ecomapp.cartservice.services.cartItemService;

import org.ecomapp.cartservice.dtos.clientsDTOs.CartItemDTO;
import org.ecomapp.cartservice.dtos.request.AddCartItemRequestDTO;
import org.ecomapp.cartservice.dtos.request.UpdateCartItemRequestDTO;
import org.ecomapp.cartservice.dtos.response.CartItemResponseDTO;

import java.util.List;

public interface CartItemService {

    List<CartItemDTO> getCartItemsByCartId(Long cartId);

    CartItemResponseDTO getCartItem(Long cartItemId);

    CartItemResponseDTO addCartItem(Long cartId, Long productVariantId, AddCartItemRequestDTO addCartItemRequestDTO);

    CartItemResponseDTO updateCartItem(Long cartItemId, UpdateCartItemRequestDTO updateCartItemRequestDTO);

    void clearCart(Long cartId);

    void deleteCartItem(Long cartItemId);
}
