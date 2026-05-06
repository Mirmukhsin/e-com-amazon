package org.ecomapp.orderservice.services.orderService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.CartItemDTO;
import org.ecomapp.orderservice.dtos.clientsDTOs.OderDTOForPayment;
import org.ecomapp.orderservice.dtos.clientsDTOs.ProductVariantDTO;
import org.ecomapp.orderservice.dtos.clientsDTOs.StockRequest;
import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressDTO;
import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressSnapshotDTO;
import org.ecomapp.orderservice.dtos.request.CheckoutRequestDTO;
import org.ecomapp.orderservice.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderservice.dtos.response.OrderResponseDTO;
import org.ecomapp.orderservice.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderservice.enums.OrderStatus;
import org.ecomapp.orderservice.enums.SubOrderStatus;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderservice.messaging.ClearCartDTO;
import org.ecomapp.orderservice.messaging.MessageProducer;
import org.ecomapp.orderservice.models.Order;
import org.ecomapp.orderservice.models.OrderItem;
import org.ecomapp.orderservice.models.SubOrder;
import org.ecomapp.orderservice.repositories.OrderItemRepository;
import org.ecomapp.orderservice.repositories.OrderRepository;
import org.ecomapp.orderservice.repositories.SubOrderRepository;
import org.ecomapp.orderservice.tolerance.CartGateway;
import org.ecomapp.orderservice.tolerance.ProductGateway;
import org.ecomapp.orderservice.tolerance.UserGateway;
import org.ecomapp.orderservice.utility.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final SubOrderRepository subOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    private final UserGateway userGateway;
    private final CartGateway cartGateway;
    private final ProductGateway productGateway;
    private final MessageProducer messageProducer;

    private static final Map<String, OrderStatus> STATUS_MAP = Map
            .of(
                    "PAID", OrderStatus.PAID,
                    "SHIPPED", OrderStatus.SHIPPED,
                    "DELIVERED", OrderStatus.DELIVERED
            );

    @Override
    public Page<OrderResponseDTO> getUserOrders(Long userId, OrderStatus orderStatus, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders;

        if (orderStatus != null) {
            orders = orderRepository.findAllByBuyerIdAndStatus(userId, orderStatus, pageable);
        } else {
            orders = orderRepository.findAllByBuyerId(userId, pageable);
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();

        List<SubOrder> subOrders = subOrderRepository.findAllByOrderIdIn(orderIds);

        Map<Long, List<SubOrder>> subOrdersByOrderId = subOrders.stream()
                .collect(Collectors.groupingBy(subOrder -> subOrder.getOrder().getId()));

        return orders.map(order -> {
            List<SubOrder> subOrderList = subOrdersByOrderId.getOrDefault(order.getId(), List.of());
            return buildOrderResponse(order, subOrderList);
        });
    }

    @Override
    public OrderResponseDTO getOrder(Long userId, Long orderId) {
        // TODO: Order
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new ResourceNotFoundException("Order not found"));

        if (!Objects.equals(order.getBuyerId(), userId)) {
            throw new UnAuthorizedException("Invalid credentials");
        }


        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(orderId);

        List<SubOrderResponseDTO> subOrderResponseDTOS = subOrders.stream()
                .map(subOrder -> {
                    SubOrderResponseDTO dto = orderMapper.subOrderResDTOFromSubOrder(subOrder);

                    List<OrderItemResponseDTO> items = orderItemRepository.findAllBySubOrder_Id(subOrder.getId())
                            .stream().map(orderMapper::orderItemResDTOFromOrderItem)
                            .toList();

                    dto.setItems(items);

                    return dto;
                }).toList();

        OrderResponseDTO orderResponseDTO = orderMapper.orderResDTOFromOrder(order);
        orderResponseDTO.setSubOrders(subOrderResponseDTOS);

        return orderResponseDTO;
    }

    @Transactional
    @Override
    public OrderResponseDTO createOrder(Long userId, CheckoutRequestDTO checkoutRequestDTO) {
        Long cartId = checkoutRequestDTO.getCartId();

        AddressDTO addressDTO = userGateway.getById(userId, checkoutRequestDTO.getAddressId());
        String addressJson;
        try {
            AddressSnapshotDTO addressSnapshotDTO = orderMapper.addressSnapshotFromAddressDTO(addressDTO);
            addressJson = objectMapper.writeValueAsString(addressSnapshotDTO);
        } catch (Exception e) {
            throw new ConflictException("Failed to serialize address");
        }

        List<CartItemDTO> cartItems = cartGateway.getItemsByCartId(cartId);

        if (cartItems.isEmpty()) {
            throw new ConflictException("Cart is empty");
        }

        Set<Long> variantIds = cartItems.stream()
                .map(CartItemDTO::getProductVariantId)
                .collect(Collectors.toSet());

        List<ProductVariantDTO> variants = productGateway.getAllByIds(variantIds);

        Map<Long, ProductVariantDTO> variantMap = variants.stream()
                .collect(Collectors.toMap(ProductVariantDTO::getId, v -> v));

        List<StockRequest> stockRequests = cartItems.stream()
                .map(item -> {

                    ProductVariantDTO variant = variantMap.get(item.getProductVariantId());

                    if (variant == null) {
                        throw new ResourceNotFoundException("Variant not found");
                    }

                    return new StockRequest(variant.getId(), item.getQuantity());
                })
                .toList();

        try {
            messageProducer.reserveStockMessage(stockRequests);

            Order order = Order.builder()
                    .buyerId(userId)
                    .shippingAddress(addressJson)
                    .status(OrderStatus.PENDING)
                    .totalAmount(0.0)
                    .build();

            orderRepository.save(order);

            Map<Long, List<CartItemDTO>> itemsBySeller = cartItems.stream()
                    .collect(Collectors.groupingBy(cartItem ->
                            variantMap.get(cartItem.getProductVariantId()).getSellerId()));

            List<SubOrder> subOrders = new ArrayList<>();

            for (Map.Entry<Long, List<CartItemDTO>> entry : itemsBySeller.entrySet()) {

                double subTotal = 0;
                List<OrderItem> subItems = new ArrayList<>();

                for (CartItemDTO item : entry.getValue()) {
                    ProductVariantDTO variant = variantMap.get(item.getProductVariantId());
                    double lineTotal = variant.getPrice() * item.getQuantity();
                    subTotal += lineTotal;

                    subItems.add(OrderItem.builder()
                            .productVariantId(variant.getId())
                            .productName(variant.getProductName())
                            .productPrice(variant.getPrice())
                            .quantity(item.getQuantity())
                            .totalPrice(lineTotal)
                            .build());
                }

                SubOrder subOrder = SubOrder.builder()
                        .order(order)
                        .sellerId(entry.getKey())
                        .status(SubOrderStatus.PENDING)
                        .subTotal(subTotal)
                        .build();

                subOrderRepository.save(subOrder);

                subItems.forEach(item -> item.setSubOrder(subOrder));
                orderItemRepository.saveAll(subItems);

                subOrders.add(subOrder);
            }

            double totalAmount = subOrders.stream().mapToDouble(SubOrder::getSubTotal).sum();

            order.setTotalAmount(totalAmount);

            orderRepository.save(order);

            messageProducer.clearCartMessage(new ClearCartDTO(cartId, userId));

            return buildOrderResponse(order, subOrders);

        } catch (Exception e) {
            messageProducer.releaseStockMessage(stockRequests);
            throw e;
        }
    }

    @Transactional
    @Override
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!Objects.equals(order.getBuyerId(), userId)) {
            throw new UnAuthorizedException("Invalid Credentials");
        }

        if (order.getStatus().equals(OrderStatus.SHIPPED) ||
                order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new ConflictException("Cannot cancel order that is already shipped or delivered");
        }

        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(orderId);

        List<StockRequest> toRelease = new ArrayList<>();

        for (SubOrder subOrder : subOrders) {

            List<OrderItem> orderItems = orderItemRepository.findAllBySubOrder_Id(subOrder.getId());

            orderItems.forEach(item -> toRelease.add(
                    new StockRequest(item.getProductVariantId(), item.getQuantity())));

            subOrder.setStatus(SubOrderStatus.CANCELLED);
        }

        messageProducer.releaseStockMessage(toRelease);

        subOrderRepository.saveAll(subOrders);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

//    TODO:     for payment service

    @Override
    public OderDTOForPayment getOrderForPayment(Long orderId) {
        return orderRepository.findById(orderId).map(orderMapper::orderDTOForPayment).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public void changeStatus(Long orderId, String orderStatus) {
        if (!STATUS_MAP.containsKey(orderStatus)) {
            throw new ConflictException("Invalid status");
        }
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(STATUS_MAP.get(orderStatus));
        orderRepository.save(order);
    }

    private OrderResponseDTO buildOrderResponse(Order order, List<SubOrder> subOrders) {
        List<SubOrderResponseDTO> subOrderResponseDTOS = new ArrayList<>();

        List<Long> subOrderIds = subOrders.stream().map(SubOrder::getId).toList();

        List<OrderItem> orderItemsBySubOrder = orderItemRepository.findAllBySubOrderIdIn(subOrderIds);

        for (SubOrder subOrder : subOrders) {

            List<OrderItem> orderItems = orderItemsBySubOrder.stream()
                    .filter(orderItem -> orderItem.getSubOrder().equals(subOrder)).toList();

            List<OrderItemResponseDTO> orderItemResponseDTOS = orderItems.stream()
                    .map(orderMapper::orderItemResDTOFromOrderItem).toList();

            SubOrderResponseDTO subOrderResponseDTO = orderMapper.subOrderResDTOFromSubOrder(subOrder);
            subOrderResponseDTO.setItems(orderItemResponseDTOS);
            subOrderResponseDTOS.add(subOrderResponseDTO);
        }

        OrderResponseDTO orderResponseDTO = orderMapper.orderResDTOFromOrder(order);
        orderResponseDTO.setSubOrders(subOrderResponseDTOS);

        return orderResponseDTO;

    }
}
