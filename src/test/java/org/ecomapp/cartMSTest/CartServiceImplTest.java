package org.ecomapp.cartMSTest;

import org.ecomapp.cartMS.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartMS.dtos.response.CartResponseDTO;
import org.ecomapp.cartMS.models.Cart;
import org.ecomapp.cartMS.models.CartItem;
import org.ecomapp.cartMS.repository.CartItemRepository;
import org.ecomapp.cartMS.repository.CartRepository;
import org.ecomapp.cartMS.services.cartService.CartServiceImpl;
import org.ecomapp.cartMS.utility.CartMapper;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CartServiceImpl cartService;

    private User buyer;
    private Cart cart;
    private CartItem cartItem;
    private CartItemResponseDTO cartItemResponseDTO;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .id(1L)
                .email("buyer@gmail.com")
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(buyer)
                .build();

        ProductVariant variant = ProductVariant.builder()
                .id(1L)
                .price(1250.0)
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .productVariant(variant)
                .quantity(2)
                .build();

        cartItemResponseDTO = CartItemResponseDTO.builder()
                .id(1L)
                .quantity(2)
                .price(1250.0)
                .subtotal(2500.0)
                .build();
    }

    // -----------------------------------------
    // GET CART TESTS
    // -----------------------------------------

    @Test
    void getCart_shouldReturnCart_whenFound() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCart_Id(1L)).thenReturn(List.of(cartItem));
        when(cartMapper.cartItemToCartItemResDto(cartItem)).thenReturn(cartItemResponseDTO);

        CartResponseDTO result = cartService.getCart(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getTotalPrice()).isEqualTo(2500.0);
    }

    @Test
    void getCart_shouldReturnEmptyCart_whenNoItems() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCart_Id(1L)).thenReturn(List.of());

        CartResponseDTO result = cartService.getCart(1L);

        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getTotalPrice()).isEqualTo(0.0);
    }

    @Test
    void getCart_shouldThrowException_whenCartNotFound() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    // -----------------------------------------
    // Create CART TESTS
    // -----------------------------------------

    @Test
    void createCart_shouldCreateCart_whenUserExists() {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(cartRepository.existsByUser_Id(1L)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToCartResDto(any(Cart.class))).thenReturn(dto);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        CartResponseDTO result = cartService.createCart();

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void createCart_shouldThrowException_whenCartAlreadyExists() {
        when(cartRepository.existsByUser_Id(1L)).thenReturn(true);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> cartService.createCart())
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already has a cart");

        verify(cartRepository, never()).save(any());
    }

    @Test
    void createCart_shouldThrowException_whenUserNotFound() {
        when(cartRepository.existsByUser_Id(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> cartService.createCart())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─────────────────────────────────────────
    // DELETE CART TESTS
    // ─────────────────────────────────────────

    @Test
    void deleteCart_shouldDeleteCart_whenFound() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.deleteCart(1L);

        verify(cartRepository).delete(cart);
    }

    @Test
    void deleteCart_shouldThrowException_whenCartNotFound() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCart(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
