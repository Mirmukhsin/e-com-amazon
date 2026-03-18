package org.ecomapp.cartMS.services.cartService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.cartMS.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartMS.dtos.response.CartResponseDTO;
import org.ecomapp.cartMS.models.Cart;
import org.ecomapp.cartMS.models.CartItem;
import org.ecomapp.cartMS.repository.CartItemRepository;
import org.ecomapp.cartMS.repository.CartRepository;
import org.ecomapp.cartMS.utility.CartMapper;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final SecurityUtils securityUtils;

    @Override
    public CartResponseDTO getCart(Long cartId) {
        // TODO: For cart response -> id, userid, items, total items, total price

        // TODO: i have id, userid
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // TODO: items
        List<CartItem> cartItems = cartItemRepository.findAllByCart_Id(cartId);

        List<CartItemResponseDTO> items = cartItems.stream().map(cartMapper::cartItemToCartItemResDto).toList();

        // TODO: total items
        Integer totalItems = items.size();

        // TODO: total price
        Double totalPrice = items.stream().mapToDouble(CartItemResponseDTO::getSubtotal).sum();

        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public CartResponseDTO createCart() {
        Long currentUserId = securityUtils.getCurrentUserId();
        boolean isCartExists = cartRepository.existsByUser_Id(currentUserId);
        if (isCartExists) {
            throw new ConflictException("User already has a cart");
        } else {

            User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Cart cart = Cart.builder().user(user).build();
            cartRepository.save(cart);

            return cartMapper.cartToCartResDto(cart);
        }
    }

    @Override
    public void deleteCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cartRepository.delete(cart);

    }
}
