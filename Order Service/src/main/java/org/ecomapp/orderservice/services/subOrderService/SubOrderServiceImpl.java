package org.ecomapp.orderservice.services.subOrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.SubOderDTOForPayment;
import org.ecomapp.orderservice.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderservice.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderservice.enums.OrderStatus;
import org.ecomapp.orderservice.enums.SubOrderStatus;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderservice.models.Order;
import org.ecomapp.orderservice.models.OrderItem;
import org.ecomapp.orderservice.models.SubOrder;
import org.ecomapp.orderservice.clients.UserMSClient;
import org.ecomapp.orderservice.repositories.OrderItemRepository;
import org.ecomapp.orderservice.repositories.OrderRepository;
import org.ecomapp.orderservice.repositories.SubOrderRepository;
import org.ecomapp.orderservice.utility.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubOrderServiceImpl implements SubOrderService {
    private final SubOrderRepository subOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserMSClient userMSClient;


    private static final Map<SubOrderStatus, Set<SubOrderStatus>> VALID_TRANSITIONS = Map.of(
            SubOrderStatus.PENDING, Set.of(SubOrderStatus.CONFIRMED, SubOrderStatus.CANCELLED),
            SubOrderStatus.CONFIRMED, Set.of(SubOrderStatus.SHIPPED, SubOrderStatus.CANCELLED),
            SubOrderStatus.SHIPPED, Set.of(SubOrderStatus.DELIVERED),
            SubOrderStatus.DELIVERED, Set.of(),
            SubOrderStatus.CANCELLED, Set.of()
    );


    @Override
    public SubOrderResponseDTO getSubOrder(Long subOrderId) {

        SubOrder subOrder = subOrderRepository.findById(subOrderId).orElseThrow(() -> new ResourceNotFoundException("Sub order not found"));

        List<OrderItemResponseDTO> orderItemResponseDTOS = orderItemRepository.findAllBySubOrder_Id(subOrder.getId())
                .stream().map(orderMapper::orderItemResDTOFromOrderItem).toList();

        SubOrderResponseDTO subOrderResponseDTO = orderMapper.subOrderResDTOFromSubOrder(subOrder);
        subOrderResponseDTO.setItems(orderItemResponseDTOS);

        return subOrderResponseDTO;
    }

    @Override
    public Page<SubOrderResponseDTO> getSellerSubOrders(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SubOrderResponseDTO> subOrderResponseDTOPage = subOrderRepository.findAllBySellerId(sellerId, pageable)
                .map(orderMapper::subOrderResDTOFromSubOrder);

        List<Long> subOrdersIds = subOrderResponseDTOPage.stream().map(SubOrderResponseDTO::getId).toList();

        List<OrderItem> orderItems = orderItemRepository.findAllBySubOrderIdIn(subOrdersIds);

        subOrderResponseDTOPage.forEach(subOrderResponseDTO -> {

            List<OrderItemResponseDTO> orderItemsBySubOrder = orderItems.stream().filter(orderItem ->
                            orderItem.getSubOrder().getId().equals(subOrderResponseDTO.getId()))
                    .map(orderMapper::orderItemResDTOFromOrderItem)
                    .toList();

            subOrderResponseDTO.setItems(orderItemsBySubOrder);
        });

        return subOrderResponseDTOPage;
    }

    @Transactional
    @Override
    public SubOrderResponseDTO updateSubOrderStatus(Long sellerId, Long subOrderId, SubOrderStatus subOrderStatus) {
        SubOrder subOrder = subOrderRepository.findById(subOrderId).orElseThrow(() -> new ResourceNotFoundException("Sub order not found"));
        Order order = subOrder.getOrder();

        // Ownership check
        if (!Objects.equals(subOrder.getSellerId(), sellerId)) {
            throw new UnAuthorizedException("You do not own this sub-order");
        }

        if (!Objects.equals(order.getStatus().toString(), "PAID")) {
            throw new ConflictException("Payment is not completed");
        }

        if (!VALID_TRANSITIONS.get(subOrder.getStatus()).contains(subOrderStatus)) {
            throw new ConflictException("Invalid status transition from %s to %s".formatted(subOrder.getStatus(), subOrderStatus));
        }

        subOrder.setStatus(subOrderStatus);
        subOrderRepository.save(subOrder);

//      TODO: seller service issue
        if (subOrderStatus.equals(SubOrderStatus.DELIVERED)) {
            Integer totalQuantityBySubOrder = getTotalQuantityBySubOrder(subOrder);
            userMSClient.updateSellerTotalSales(sellerId, totalQuantityBySubOrder);
        }

        List<SubOrder> subOrders = subOrderRepository.findAllByOrderId(subOrder.getOrder().getId());
        boolean allDelivered = subOrders.stream().allMatch(subOrder1 -> subOrder1.getStatus().equals(SubOrderStatus.DELIVERED));
        boolean allConfirmedOrBeyond = subOrders.stream()
                .allMatch(s -> s.getStatus().ordinal() >= SubOrderStatus.CONFIRMED.ordinal());

        if (allDelivered) {
            order.setStatus(OrderStatus.DELIVERED);
        } else if (allConfirmedOrBeyond) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        orderRepository.save(order);

        return orderMapper.subOrderResDTOFromSubOrder(subOrder);
    }

//  TODO:  for Payment

    @Override
    public SubOderDTOForPayment getSubOrderForPayment(Long subOrderId) {
        return subOrderRepository.findById(subOrderId).map(orderMapper::subOrderDTOForPayment)
                .orElseThrow(() -> new ResourceNotFoundException("Sub order not found"));
    }

    private Integer getTotalQuantityBySubOrder(SubOrder subOrder) {
        List<OrderItem> orderItems = orderItemRepository.findAllBySubOrder_Id(subOrder.getId());

        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }
}
