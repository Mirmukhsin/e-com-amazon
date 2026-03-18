package org.ecomapp.orderMS.services.orderService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.ecomapp.orderMS.utility.OrderMapper;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.productMS.repositories.ProductVariantRepository;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.dtos.response.AddressSnapshotDTO;
import org.ecomapp.userMS.models.Address;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.AddressRepository;
import org.ecomapp.userMS.utility.AddressMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final SubOrderRepository subOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    private final AddressMapper addressMapper;
    private final ProductVariantRepository productVariantRepository;
    private final SecurityUtils securityUtils;

    private final Long CURRENT_USER_ID = securityUtils.getCurrentUserId();

    @Override
    public Page<OrderResponseDTO> getUserOrders(OrderStatus orderStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders;

        if (orderStatus != null) {
            orders = orderRepository.findAllByBuyer_IdAndStatus(CURRENT_USER_ID, orderStatus, pageable);
        } else {
            orders = orderRepository.findAllByBuyer_Id(CURRENT_USER_ID, pageable);
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();

        List<SubOrder> subOrders = subOrderRepository.findAllByOrderIdIn(orderIds);

        Map<Long, List<SubOrder>> subOrdersByOrderId = subOrders.stream().collect(Collectors.groupingBy(subOrder -> subOrder.getOrder().getId()));

        return orders.map(order -> {
            List<SubOrder> subOrderList = subOrdersByOrderId.getOrDefault(order.getId(), List.of());
            return buildOrderResponse(order, subOrderList);
        });
    }

    @Override
    public OrderResponseDTO getOrder(Long orderId) {
        // TODO: Order
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!Objects.equals(order.getBuyer().getId(), CURRENT_USER_ID)) {
            throw new UnAuthorizedException("Invalid credentials");
        } else {

            // TODO: sub order of an Order
            List<SubOrderResponseDTO> subOrderResponseDTOS = subOrderRepository.findAllByOrder_Id(orderId).stream().map(orderMapper::subOrderToSubOrderResDto).toList();

            for (SubOrderResponseDTO subOrderResponseDTO : subOrderResponseDTOS) {

                // TODO: items of a sub order
                List<OrderItemResponseDTO> items = orderItemRepository.findAllBySubOrder_Id(subOrderResponseDTO.getId()).stream().map(orderMapper::orderItemToOrderItemResDto).toList();

                subOrderResponseDTO.setItems(items);
            }

            OrderResponseDTO orderResponseDTO = orderMapper.orderToOrderResDto(order);
            orderResponseDTO.setSubOrders(subOrderResponseDTOS);

            return orderResponseDTO;
        }
    }

    @Transactional
    @Override
    public OrderResponseDTO createOrder(CheckoutRequestDTO checkoutRequestDTO) {

        Long cartId = checkoutRequestDTO.getCartId();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (!cart.getUser().getId().equals(CURRENT_USER_ID)) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        Address address = addressRepository.findById(checkoutRequestDTO.getAddressId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(CURRENT_USER_ID)) {
            throw new UnAuthorizedException("Invalid credentials");
        }

        AddressSnapshotDTO addressSnapshotDTO = addressMapper.addressToAddressSnapDto(address);
        String addressJson;
        try {
            addressJson = objectMapper.writeValueAsString(addressSnapshotDTO);
        } catch (Exception e) {
            throw new ConflictException("Failed to serialize address");
        }

        List<CartItem> cartItems = cartItemRepository.findAllByCart_Id(cartId);

        if (cartItems.isEmpty()) {
            throw new ConflictException("Cart is empty");
        }

        Order order = Order.builder()
                .buyer(cart.getUser())
                .shippingAddress(addressJson)
                .status(OrderStatus.PENDING)
                .totalAmount(0.0)
                .build();

        orderRepository.save(order);


        Map<User, List<CartItem>> itemsBySeller = cartItems.stream().collect(Collectors.groupingBy(cartItem -> cartItem.getProductVariant().getProduct().getSeller()));

        List<SubOrder> subOrders = new ArrayList<>();

        for (Map.Entry<User, List<CartItem>> entry : itemsBySeller.entrySet()) {

            User seller = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();
            SubOrder subOrder = SubOrder.builder()
                    .order(order)
                    .seller(seller)
                    .status(SubOrderStatus.PENDING)
                    .subTotal(0.0)
                    .build();

            subOrderRepository.save(subOrder);

            double subTotal = 0;

            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem sellerItem : sellerItems) {

                ProductVariant productVariant = sellerItem.getProductVariant();

                if (productVariant.getStockQuantity() < sellerItem.getQuantity()) {
                    throw new ConflictException("Insufficient stock for: %s".formatted(productVariant.getProduct().getName()));
                }

                OrderItem orderItem = OrderItem.builder()
                        .subOrder(subOrder)
                        .productVariant(productVariant)
                        .productName(productVariant.getProduct().getName())
                        .productPrice(productVariant.getPrice())
                        .quantity(sellerItem.getQuantity())
                        .totalPrice(productVariant.getPrice() * sellerItem.getQuantity())
                        .build();

                orderItemRepository.save(orderItem);

                orderItems.add(orderItem);

                productVariant.setStockQuantity(productVariant.getStockQuantity() - sellerItem.getQuantity());
                productVariantRepository.save(productVariant);

                subTotal += orderItem.getTotalPrice();
            }

            subOrder.setSubTotal(subTotal);
            subOrderRepository.save(subOrder);
            subOrders.add(subOrder);
        }

        double totalAmount = subOrders.stream().mapToDouble(SubOrder::getSubTotal).sum();

        order.setTotalAmount(totalAmount);

        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return buildOrderResponse(order, subOrders);
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus().equals(OrderStatus.SHIPPED) ||
                order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new ConflictException("Cannot cancel order that is already shipped or delivered");
        }

        List<SubOrder> subOrders = subOrderRepository.findAllByOrder_Id(orderId);

        for (SubOrder subOrder : subOrders) {

            List<OrderItem> orderItems = orderItemRepository.findAllBySubOrder_Id(subOrder.getId());

            for (OrderItem orderItem : orderItems) {
                ProductVariant productVariant = orderItem.getProductVariant();
                productVariant.setStockQuantity(productVariant.getStockQuantity() + orderItem.getQuantity());
                productVariantRepository.save(productVariant);
            }

            subOrder.setStatus(SubOrderStatus.CANCELLED);
        }
        subOrderRepository.saveAll(subOrders);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private OrderResponseDTO buildOrderResponse(Order order, List<SubOrder> subOrders) {
        List<SubOrderResponseDTO> subOrderResponseDTOS = new ArrayList<>();

        List<Long> subOrderIds = subOrders.stream().map(SubOrder::getId).toList();

        List<OrderItem> orderItemsBySubOrder = orderItemRepository.findAllBySubOrderIdIn(subOrderIds);

        for (SubOrder subOrder : subOrders) {

            List<OrderItem> orderItems = orderItemsBySubOrder.stream().filter(orderItem -> orderItem.getSubOrder().equals(subOrder)).toList();

            List<OrderItemResponseDTO> orderItemResponseDTOS = orderItems.stream().map(orderMapper::orderItemToOrderItemResDto).toList();

            SubOrderResponseDTO subOrderResponseDTO = orderMapper.subOrderToSubOrderResDto(subOrder);
            subOrderResponseDTO.setItems(orderItemResponseDTOS);
            subOrderResponseDTOS.add(subOrderResponseDTO);
        }

        OrderResponseDTO orderResponseDTO = orderMapper.orderToOrderResDto(order);
        orderResponseDTO.setSubOrders(subOrderResponseDTOS);

        return orderResponseDTO;

    }
}
