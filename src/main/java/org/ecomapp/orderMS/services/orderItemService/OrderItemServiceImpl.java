package org.ecomapp.orderMS.services.orderItemService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.orderMS.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderMS.repositories.OrderItemRepository;
import org.ecomapp.orderMS.utility.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderItemResponseDTO> getOrderItems(Long subOrderId) {
        return orderItemRepository.findAllBySubOrder_Id(subOrderId).stream().map(orderMapper::orderItemToOrderItemResDto).toList();
    }
}
