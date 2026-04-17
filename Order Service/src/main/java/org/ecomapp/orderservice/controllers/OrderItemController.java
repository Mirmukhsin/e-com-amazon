package org.ecomapp.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.OrderItemDTOForReview;
import org.ecomapp.orderservice.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderservice.services.orderItemService.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders/sub-orders")
@RequiredArgsConstructor
@Tag(name = "E. Order items", description = "Order item management")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @Operation(summary = "Get sub order items")
    @GetMapping("/{subOrderId}/items")
    public ResponseEntity<List<OrderItemResponseDTO>> getOrderItems(@PathVariable Long subOrderId) {
        return new ResponseEntity<>(orderItemService.getOrderItems(subOrderId), HttpStatus.OK);
    }

//   TODO: for review

    @Operation(summary = "get order item - from review")
    @GetMapping("/items/{orderItemId}")
    public ResponseEntity<OrderItemDTOForReview> forReview(@PathVariable Long orderItemId) {
        return new ResponseEntity<>(orderItemService.forReview(orderItemId), HttpStatus.OK);
    }
}
