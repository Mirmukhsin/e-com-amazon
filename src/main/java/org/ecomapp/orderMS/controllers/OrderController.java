package org.ecomapp.orderMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderMS.dtos.request.CheckoutRequestDTO;
import org.ecomapp.orderMS.dtos.response.OrderResponseDTO;
import org.ecomapp.orderMS.enums.OrderStatus;
import org.ecomapp.orderMS.services.orderService.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "12. Orders", description = "Order management")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get all user orders")
    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getUserOrders(@RequestParam(required = false) OrderStatus orderStatus,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(orderService.getUserOrders(orderStatus, page, size), HttpStatus.OK);
    }

    @Operation(summary = "Get order by id")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrder(orderId), HttpStatus.OK);
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
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        return new ResponseEntity<>(orderService.createOrder(checkoutRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel order by id")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
