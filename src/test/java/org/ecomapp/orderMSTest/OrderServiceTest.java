package org.ecomapp.orderMSTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecomapp.cartMS.models.Cart;
import org.ecomapp.cartMS.models.CartItem;
import org.ecomapp.cartMS.repository.CartItemRepository;
import org.ecomapp.cartMS.repository.CartRepository;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderMS.dtos.request.CheckoutRequestDTO;
import org.ecomapp.orderMS.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderMS.dtos.response.OrderResponseDTO;
import org.ecomapp.orderMS.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderMS.enums.OrderStatus;
import org.ecomapp.orderMS.enums.SubOrderStatus;
import org.ecomapp.orderMS.models.Order;
import org.ecomapp.orderMS.models.OrderItem;
import org.ecomapp.orderMS.models.SubOrder;
import org.ecomapp.orderMS.repositories.OrderItemRepository;
import org.ecomapp.orderMS.repositories.OrderRepository;
import org.ecomapp.orderMS.repositories.SubOrderRepository;
import org.ecomapp.orderMS.services.orderService.OrderServiceImpl;
import org.ecomapp.orderMS.utility.OrderMapper;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.productMS.repositories.ProductVariantRepository;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.dtos.response.AddressSnapshotDTO;
import org.ecomapp.userMS.models.Address;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.AddressRepository;
import org.ecomapp.userMS.utility.AddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    SubOrderRepository subOrderRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    AddressRepository addressRepository;

    @Mock
    ProductVariantRepository productVariantRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    SecurityUtils securityUtils;

    @Mock
    AddressMapper addressMapper;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    OrderServiceImpl orderService;

    private User buyer;
    private User seller;
    private Cart cart;
    private Address address;
    private Product product;
    private ProductVariant productVariant;
    private CartItem cartItem;
    private Order order;
    private SubOrder subOrder;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .id(1L)
                .email("buyer@gmail.com")
                .build();
        seller = User.builder()
                .id(2L)
                .email("seller@gmail.com")
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(buyer)
                .build();

        address = Address.builder()
                .id(1L)
                .state("123 Main St")
                .city("New York")
                .country("USA")
                .user(buyer)
                .build();

        product = Product.builder()
                .id(1L)
                .name("ROG Laptop")
                .seller(seller)
                .build();

        productVariant = ProductVariant.builder()
                .id(1L)
                .sku("ROG-001")
                .price(1250.0)
                .stockQuantity(10)
                .product(product)
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .productVariant(productVariant)
                .quantity(2)
                .build();

        order = Order.builder()
                .id(1L)
                .buyer(buyer)
                .status(OrderStatus.PENDING)
                .totalAmount(2500.0)
                .build();

        subOrder = SubOrder.builder()
                .id(1L)
                .order(order)
                .seller(seller)
                .status(SubOrderStatus.PENDING)
                .subTotal(2500.0)
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .subOrder(subOrder)
                .productVariant(productVariant)
                .productName("ROG Laptop")
                .productPrice(1250.0)
                .quantity(2)
                .totalPrice(2500.0)
                .build();
    }

    // ---------------------------------------
    // Create order tests
    // ---------------------------------------

    @Test
    void createOrder_whenValid() {

        CheckoutRequestDTO dto = new CheckoutRequestDTO();
        dto.setCartId(1L);
        dto.setAddressId(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(cartItemRepository.findAllByCart_Id(1L)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(subOrderRepository.save(any(SubOrder.class))).thenReturn(subOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        when(orderItemRepository.findAllBySubOrderIdIn(any())).thenReturn(List.of(orderItem));

        when(orderMapper.orderToOrderResDto(any(Order.class))).thenReturn(new OrderResponseDTO());
        when(orderMapper.subOrderToSubOrderResDto(any(SubOrder.class))).thenReturn(new SubOrderResponseDTO());

        // ??
        when(addressMapper.addressToAddressSnapDto(address)).thenReturn(new AddressSnapshotDTO());

        // ??
        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        OrderResponseDTO result = orderService.createOrder(dto);

        assertThat(result).isNotNull();

        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(cartItemRepository).deleteAll(List.of(cartItem));
    }

    @Test
    void createOrder_whenCartNotBelongToUser() {

        CheckoutRequestDTO dto = new CheckoutRequestDTO();
        dto.setCartId(1L);
        dto.setAddressId(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    void createOrder_whenCartEmpty() {
        CheckoutRequestDTO dto = new CheckoutRequestDTO();
        dto.setCartId(1L);
        dto.setAddressId(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(cartItemRepository.findAllByCart_Id(1L)).thenReturn(new ArrayList<>());

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("empty");
    }


    @Test
    void createOrder_whenInsufficientStock() throws JsonProcessingException {

        CheckoutRequestDTO dto = new CheckoutRequestDTO();
        dto.setCartId(1L);
        dto.setAddressId(1L);

        productVariant.setStockQuantity(1); // less than needed

        cartItem.setQuantity(5);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(cartItemRepository.findAllByCart_Id(1L)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(subOrderRepository.save(any(SubOrder.class))).thenReturn(subOrder);

        when(addressMapper.addressToAddressSnapDto(address)).thenReturn(new AddressSnapshotDTO());
        when(objectMapper.writeValueAsString(any(AddressSnapshotDTO.class))).thenReturn("{json}");

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("stock");
    }

    @Test
    void createOrder_whenAddressNotBelongToUser() {
        CheckoutRequestDTO dto = new CheckoutRequestDTO();
        dto.setCartId(1L);
        dto.setAddressId(1L);

        User anotherUser = User.builder().id(99L).build();
        Address anotherAddress = Address.builder().id(1L).user(anotherUser).build();

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(anotherAddress));

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    void createOrder_whenCartNotFound() {
        CheckoutRequestDTO dto = new CheckoutRequestDTO();
        dto.setCartId(1L);
        dto.setAddressId(1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cart not found");
    }

    // ---------------------------------------
    // Cancel order tests
    // ---------------------------------------


    @Test
    void cancelOrder_whenStatusPending() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(subOrderRepository.findAllByOrder_Id(1L)).thenReturn(List.of(subOrder));
        when(orderItemRepository.findAllBySubOrder_Id(1L)).thenReturn(List.of(orderItem));

        orderService.cancelOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(subOrder.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
        verify(subOrderRepository).saveAll(any());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_shouldRestoreStock_whenCancelled() {
        int originalStock = productVariant.getStockQuantity();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(subOrderRepository.findAllByOrder_Id(1L)).thenReturn(List.of(subOrder));
        when(orderItemRepository.findAllBySubOrder_Id(1L)).thenReturn(List.of(orderItem));

        orderService.cancelOrder(1L);

        assertThat(productVariant.getStockQuantity())
                .isEqualTo(originalStock + orderItem.getQuantity());
    }

    @Test
    void cancelOrder_shouldThrowException_whenOrderAlreadyShipped() {
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("shipped");
    }

    @Test
    void cancelOrder_shouldThrowException_whenOrderDelivered() {
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void cancelOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    // ---------------------------------------
    // Get order tests
    // ---------------------------------------

    @Test
    void getOrder_shouldReturnOrder_whenBelongsToUser() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(subOrderRepository.findAllByOrder_Id(1L)).thenReturn(List.of(subOrder));
        when(orderItemRepository.findAllBySubOrder_Id(1L)).thenReturn(List.of(orderItem));

        when(orderMapper.orderToOrderResDto(order)).thenReturn(new OrderResponseDTO());
        when(orderMapper.subOrderToSubOrderResDto(subOrder)).thenReturn(new SubOrderResponseDTO());
        when(orderMapper.orderItemToOrderItemResDto(orderItem)).thenReturn(new OrderItemResponseDTO());

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        OrderResponseDTO result = orderService.getOrder(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getOrder_shouldThrowException_whenNotBelongsToUser() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(securityUtils.getCurrentUserId()).thenReturn(99L);

        assertThatThrownBy(() -> orderService.getOrder(1L))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    void getOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
