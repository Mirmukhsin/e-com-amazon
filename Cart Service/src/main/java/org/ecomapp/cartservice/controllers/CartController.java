package org.ecomapp.cartservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.cartservice.dtos.response.CartResponseDTO;
import org.ecomapp.cartservice.services.cartService.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "A. Cart", description = "Shopping cart management")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Get cart by id")
    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long cartId) {
        return new ResponseEntity<>(cartService.getCart(cartId), HttpStatus.OK);
    }

    @Operation(summary = "Create cart")
    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart(@RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(cartService.createCart(userId), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete cart")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long cartId) {
        cartService.deleteCart(userId,cartId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
