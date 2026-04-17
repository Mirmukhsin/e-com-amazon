package org.ecomapp.cartservice.services.cartItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.cartservice.dtos.clientsDTOs.CartItemDTO;
import org.ecomapp.cartservice.dtos.request.AddCartItemRequestDTO;
import org.ecomapp.cartservice.dtos.request.UpdateCartItemRequestDTO;
import org.ecomapp.cartservice.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartservice.dtos.clientsDTOs.ProductVariantDTO;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.cartservice.models.Cart;
import org.ecomapp.cartservice.models.CartItem;
import org.ecomapp.cartservice.clients.ProductMSClient;
import org.ecomapp.cartservice.repository.CartItemRepository;
import org.ecomapp.cartservice.repository.CartRepository;
import org.ecomapp.cartservice.utility.CartMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductMSClient productMSClient;

    @Override
    public List<CartItemDTO> getCartItemsByCartId(Long cartId) {
        return cartItemRepository.findAllByCart_Id(cartId).stream().map(cartMapper::cartItemDTOFromCartItem).toList();
    }

    @Override
    public CartItemResponseDTO getCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        ProductVariantDTO productVariant = productMSClient.getById(cartItem.getProductVariantId());

        return cartMapper.cartItemResponseFromCartItem(cartItem, productVariant);
    }

    @Transactional
    @Override
    public CartItemResponseDTO addCartItem(Long cartId, Long productVariantId, AddCartItemRequestDTO addCartItemRequestDTO) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        ProductVariantDTO productVariant = productMSClient.getById(productVariantId);

        int requestedQuantity = addCartItemRequestDTO.getQuantity();

        Optional<CartItem> existing = cartItemRepository.findByCart_IdAndProductVariantId(cartId, productVariantId);

        if (existing.isPresent()) {
            CartItem existingItem = existing.get();
            int newQuantity = existingItem.getQuantity() + requestedQuantity;

            if (productVariant.getStockQuantity() < newQuantity) {
                throw new ConflictException("Insufficient stock");
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
            return cartMapper.cartItemResponseFromCartItem(existingItem, productVariant);

        } else {

            if (productVariant.getStockQuantity() < requestedQuantity) {
                throw new ConflictException("Insufficient stock");
            }

            CartItem cartItem = CartItem.builder()
                    .quantity(addCartItemRequestDTO.getQuantity())
                    .cart(cart)
                    .productVariantId(productVariant.getId())
                    .build();

            cartItemRepository.save(cartItem);
            return cartMapper.cartItemResponseFromCartItem(cartItem, productVariant);
        }
    }

    @Override
    public CartItemResponseDTO updateCartItem(Long cartItemId, UpdateCartItemRequestDTO updateCartItemRequestDTO) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        ProductVariantDTO productVariant = productMSClient.getById(cartItem.getProductVariantId());

        if (productVariant.getStockQuantity() < updateCartItemRequestDTO.getQuantity()) {
            throw new ConflictException("Insufficient stock");
        }
        cartItem.setQuantity(updateCartItemRequestDTO.getQuantity());
        cartItemRepository.save(cartItem);

        return cartMapper.cartItemResponseFromCartItem(cartItem, productVariant);
    }

    @Override
    public void clearCart(Long cartId) {
        List<CartItem> items = cartItemRepository.findAllByCart_Id(cartId);
        cartItemRepository.deleteAll(items);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }
}
