package org.ecomapp.cartMS.services.cartItemService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.cartMS.dtos.request.AddCartItemRequestDTO;
import org.ecomapp.cartMS.dtos.request.UpdateCartItemRequestDTO;
import org.ecomapp.cartMS.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartMS.models.Cart;
import org.ecomapp.cartMS.models.CartItem;
import org.ecomapp.cartMS.repository.CartItemRepository;
import org.ecomapp.cartMS.repository.CartRepository;
import org.ecomapp.cartMS.utility.CartMapper;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.productMS.repositories.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartMapper cartMapper;

    @Override
    public CartItemResponseDTO getCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        return cartMapper.cartItemToCartItemResDto(cartItem);
    }

    @Override
    public CartItemResponseDTO addCartItem(Long cartId, Long productVariantId, AddCartItemRequestDTO addCartItemRequestDTO) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        ProductVariant productVariant = productVariantRepository.findById(productVariantId).orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        int requestedQuantity = addCartItemRequestDTO.getQuantity();

        Optional<CartItem> existing = cartItemRepository.findByCart_IdAndProductVariant_Id(cartId, productVariantId);

        if (existing.isPresent()) {
            CartItem existingItem = existing.get();
            int newQuantity = existingItem.getQuantity() + requestedQuantity;

            if (productVariant.getStockQuantity() < newQuantity) {
                throw new ConflictException("Insufficient stock");
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
            return cartMapper.cartItemToCartItemResDto(existingItem);

        } else {

            if (productVariant.getStockQuantity() < requestedQuantity) {
                throw new ConflictException("Insufficient stock");
            }

            CartItem cartItem = CartItem.builder()
                    .quantity(addCartItemRequestDTO.getQuantity())
                    .cart(cart)
                    .productVariant(productVariant)
                    .build();

            cartItemRepository.save(cartItem);
            return cartMapper.cartItemToCartItemResDto(cartItem);
        }
    }

    @Override
    public CartItemResponseDTO updateCartItem(Long cartItemId, UpdateCartItemRequestDTO updateCartItemRequestDTO) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (cartItem.getProductVariant().getStockQuantity() < updateCartItemRequestDTO.getQuantity()) {
            throw new ConflictException("Insufficient stock");
        }
        cartItem.setQuantity(updateCartItemRequestDTO.getQuantity());
        cartItemRepository.save(cartItem);

        return cartMapper.cartItemToCartItemResDto(cartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }
}
