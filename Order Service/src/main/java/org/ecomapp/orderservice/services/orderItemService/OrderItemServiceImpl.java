package org.ecomapp.orderservice.services.orderItemService;

import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.OrderItemDTOForReview;
import org.ecomapp.orderservice.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.orderservice.repositories.OrderItemRepository;
import org.ecomapp.orderservice.utility.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderItemResponseDTO> getOrderItems(Long subOrderId) {
        return orderItemRepository.findAllBySubOrder_Id(subOrderId)
                .stream().map(orderMapper::orderItemResDTOFromOrderItem).toList();
    }

//  TODO:  for review

    @Override
    public OrderItemDTOForReview forReview(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).map(orderMapper::orderItemDTOForReview)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
    }
}
