package org.ecomapp.orderMS.services.subOrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.orderMS.dtos.response.OrderItemResponseDTO;
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
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubOrderServiceImpl implements SubOrderService {
    private final SubOrderRepository subOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final SecurityUtils securityUtils;

    @Override
    public SubOrderResponseDTO getSubOrder(Long subOrderId) {

        SubOrder subOrder = subOrderRepository.findById(subOrderId).orElseThrow(() -> new ResourceNotFoundException("Sub order not found"));

        List<OrderItemResponseDTO> orderItemResponseDTOS = orderItemRepository.findAllBySubOrder_Id(subOrder.getId()).stream().map(orderMapper::orderItemToOrderItemResDto).toList();

        SubOrderResponseDTO subOrderResponseDTO = orderMapper.subOrderToSubOrderResDto(subOrder);
        subOrderResponseDTO.setItems(orderItemResponseDTOS);

        return subOrderResponseDTO;
    }

    @Override
    public Page<SubOrderResponseDTO> getSellerSubOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SubOrderResponseDTO> subOrderResponseDTOPage = subOrderRepository.findAllBySeller_Id(securityUtils.getCurrentUserId(), pageable).map(orderMapper::subOrderToSubOrderResDto);

        List<Long> subOrdersIds = subOrderResponseDTOPage.stream().map(SubOrderResponseDTO::getId).toList();

        List<OrderItem> orderItems = orderItemRepository.findAllBySubOrderIdIn(subOrdersIds);

        subOrderResponseDTOPage.forEach(subOrderResponseDTO -> {

            List<OrderItemResponseDTO> orderItemsBySubOrder = orderItems.stream().filter(orderItem ->
                            orderItem.getSubOrder().getId().equals(subOrderResponseDTO.getId()))
                    .map(orderMapper::orderItemToOrderItemResDto)
                    .toList();

            subOrderResponseDTO.setItems(orderItemsBySubOrder);
        });

        return subOrderResponseDTOPage;
    }

    @Transactional
    @Override
    public SubOrderResponseDTO updateSubOrderStatus(Long subOrderId, SubOrderStatus subOrderStatus) {
        SubOrder subOrder = subOrderRepository.findById(subOrderId).orElseThrow(() -> new ResourceNotFoundException("Sub order not found"));

        Map<SubOrderStatus, List<SubOrderStatus>> validTransition = Map.of(
                SubOrderStatus.PENDING, List.of(SubOrderStatus.CONFIRMED, SubOrderStatus.CANCELLED),
                SubOrderStatus.CONFIRMED, List.of(SubOrderStatus.SHIPPED, SubOrderStatus.CANCELLED),
                SubOrderStatus.SHIPPED, List.of(SubOrderStatus.DELIVERED, SubOrderStatus.CANCELLED),
                SubOrderStatus.DELIVERED, List.of(),
                SubOrderStatus.CANCELLED, List.of()
        );

        if (!validTransition.get(subOrder.getStatus()).contains(subOrderStatus)) {
            throw new ConflictException("Invalid status transition from %s to %s".formatted(subOrder.getStatus(), subOrderStatus));
        }

        subOrder.setStatus(subOrderStatus);
        subOrderRepository.save(subOrder);

        List<SubOrder> subOrders = subOrderRepository.findAllByOrder_Id(subOrder.getOrder().getId());
        boolean allDelivered = subOrders.stream().allMatch(subOrder1 -> subOrder1.getStatus().equals(SubOrderStatus.DELIVERED));

        if (allDelivered) {
            Order order = orderRepository.findById(subOrder.getOrder().getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        return orderMapper.subOrderToSubOrderResDto(subOrder);
    }
}
