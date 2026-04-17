package org.ecomapp.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.OderDTOForPayment;
import org.ecomapp.orderservice.dtos.request.CheckoutRequestDTO;
import org.ecomapp.orderservice.dtos.response.OrderResponseDTO;
import org.ecomapp.orderservice.enums.OrderStatus;
import org.ecomapp.orderservice.services.orderService.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "C. Orders", description = "Order management")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get all user orders")
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getUserOrders(@RequestHeader("X-User-Id") Long userId,
                                                                @RequestParam(required = false) OrderStatus orderStatus,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(orderService.getUserOrders(userId, orderStatus, page, size), HttpStatus.OK);
    }

    @Operation(summary = "Get order by id")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@RequestHeader("X-User-Id") Long userId, @PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrder(userId, orderId), HttpStatus.OK);
    }

    @Operation(
            summary = "Create order",
            description = "Creates order from cart - Buyer only"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cart is empty"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        return new ResponseEntity<>(orderService.createOrder(userId, checkoutRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel order by id")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestHeader("X-User-Id") Long userId, @PathVariable Long orderId) {
        orderService.cancelOrder(userId, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Change status")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long orderId, @RequestBody String orderStatus) {
        orderService.changeStatus(orderId, orderStatus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//   TODO: for payment service

    @Operation(summary = "get order - from payment")
    @GetMapping("/forPayment/{orderId}")
    public ResponseEntity<OderDTOForPayment> forPayment(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrderForPayment(orderId), HttpStatus.OK);
    }

}
