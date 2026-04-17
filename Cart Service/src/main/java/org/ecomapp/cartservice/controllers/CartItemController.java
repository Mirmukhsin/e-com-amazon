package org.ecomapp.cartservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.cartservice.dtos.clientsDTOs.CartItemDTO;
import org.ecomapp.cartservice.dtos.request.AddCartItemRequestDTO;
import org.ecomapp.cartservice.dtos.request.UpdateCartItemRequestDTO;
import org.ecomapp.cartservice.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartservice.services.cartItemService.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts/items")
@RequiredArgsConstructor
@Tag(name = "B. Cart items", description = "Cart item management")
public class CartItemController {
    private final CartItemService cartItemService;

    @Operation(summary = "Get cart item")
    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponseDTO> getCartItem(@PathVariable Long cartItemId) {
        return new ResponseEntity<>(cartItemService.getCartItem(cartItemId), HttpStatus.OK);
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/{cartId}/add/{variantId}")
    public ResponseEntity<CartItemResponseDTO> addItem(@PathVariable Long cartId,
                                                       @PathVariable Long variantId,
                                                       @Valid @RequestBody AddCartItemRequestDTO addCartItemRequestDTO) {
        return new ResponseEntity<>(cartItemService.addCartItem(cartId, variantId, addCartItemRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update item details")
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponseDTO> updateCartItem(@PathVariable Long cartItemId,
                                                              @Valid @RequestBody UpdateCartItemRequestDTO updateCartItemRequestDTO) {
        return new ResponseEntity<>(cartItemService.updateCartItem(cartItemId, updateCartItemRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Delete item")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //TODO: Called by Order Service after checkout — clears all items in a cart
    @Operation(summary = "Clear cart - from order service")
    @DeleteMapping("/{cartId}/clear")   // <-- NEW: was missing
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartItemService.clearCart(cartId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "get all cart item by cart id - from order service")
    @GetMapping("/by/{cartId}")
    public ResponseEntity<List<CartItemDTO>> getAllByCartId(@PathVariable Long cartId) {
        return new ResponseEntity<>(cartItemService.getCartItemsByCartId(cartId), HttpStatus.OK);
    }

}
