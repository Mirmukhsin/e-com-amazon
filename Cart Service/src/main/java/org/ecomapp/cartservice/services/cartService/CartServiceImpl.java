package org.ecomapp.cartservice.services.cartService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.cartservice.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartservice.dtos.response.CartResponseDTO;
import org.ecomapp.cartservice.dtos.clientsDTOs.ProductVariantDTO;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.cartservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.cartservice.models.Cart;
import org.ecomapp.cartservice.models.CartItem;
import org.ecomapp.cartservice.clients.ProductMSClient;
import org.ecomapp.cartservice.repository.CartItemRepository;
import org.ecomapp.cartservice.repository.CartRepository;
import org.ecomapp.cartservice.utility.CartMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductMSClient productMSClient;

    @Override
    public CartResponseDTO getCart(Long cartId) {
        // TODO: For cart response -> id, userid, items, total items, total price

        // TODO: i have id, userid
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // TODO: items
        Set<Long> variantIds = cart.getItems().stream().map(CartItem::getProductVariantId).collect(Collectors.toSet());

        List<ProductVariantDTO> variantByIds = productMSClient.getAllByIds(variantIds);

        Map<Long, ProductVariantDTO> variantsMap = variantByIds.stream().collect(Collectors.toMap(ProductVariantDTO::getId, v -> v));

        List<CartItemResponseDTO> items = cart.getItems().stream().map(cartItem -> {
            ProductVariantDTO variantDTO = variantsMap.get(cartItem.getProductVariantId());
            return cartMapper.cartItemResponseFromCartItem(cartItem, variantDTO);
        }).toList();

        // TODO: total items
        Integer totalItems = items.size();

        // TODO: total price
        Double totalPrice = items.stream().mapToDouble(CartItemResponseDTO::getSubtotal).sum();

        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public CartResponseDTO createCart(Long userId) {
        boolean isCartExists = cartRepository.existsByUserId(userId);
        if (isCartExists) {
            throw new ConflictException("User already has a cart");
        } else {

            Cart cart = Cart.builder().userId(userId).build();
            cartRepository.save(cart);

            return cartMapper.cartResponseFromCart(cart);
        }
    }

    @Override
    public void deleteCart(Long userId,Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (!Objects.equals(userId,cart.getUserId())){
            throw new UnAuthorizedException("Invalid Credentials");
        }
        cartRepository.delete(cart);

    }
}
