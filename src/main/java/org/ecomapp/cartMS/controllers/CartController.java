package org.ecomapp.cartMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.cartMS.dtos.response.CartResponseDTO;
import org.ecomapp.cartMS.services.cartService.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "10. Cart", description = "Shopping cart management")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Get cart by id")
    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long cartId) {
        return new ResponseEntity<>(cartService.getCart(cartId), HttpStatus.OK);
    }

    @Operation(summary = "Create cart")
    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart() {
        return new ResponseEntity<>(cartService.createCart(), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete cart")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
