package org.ecomapp.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderservice.dtos.clientsDTOs.SubOderDTOForPayment;
import org.ecomapp.orderservice.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderservice.enums.SubOrderStatus;
import org.ecomapp.orderservice.services.subOrderService.SubOrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/sub-orders")
@RequiredArgsConstructor
@Tag(name = "D. Sub orders", description = "Sub order management")
public class SubOrderController {
    private final SubOrderService subOrderService;

    @Operation(summary = "Get sub order")
    @GetMapping("/{subOrderId}")
    public ResponseEntity<SubOrderResponseDTO> getSubOrder(@PathVariable Long subOrderId) {
        return new ResponseEntity<>(subOrderService.getSubOrder(subOrderId), HttpStatus.OK);
    }

    @Operation(summary = "Get Seller sub orders - SELLER only")
    @GetMapping("/me")
    public ResponseEntity<Page<SubOrderResponseDTO>> getSellerSubOrders(@RequestHeader("X-User-Id") Long sellerId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(subOrderService.getSellerSubOrders(sellerId, page, size), HttpStatus.OK);
    }

    @Operation(summary = "Update sub order status - SELLER only")
    @PatchMapping("/{subOrderId}/status")
    public ResponseEntity<SubOrderResponseDTO> updateSubOrderStatus(@RequestHeader("X-User-Id") Long sellerId,
                                                                    @PathVariable Long subOrderId,
                                                                    @RequestBody SubOrderStatus subOrderStatus) {
        return new ResponseEntity<>(subOrderService.updateSubOrderStatus(sellerId, subOrderId, subOrderStatus), HttpStatus.OK);
    }

//  TODO:  for payment

    @Operation(summary = "get sub order - from payment")
    @GetMapping("/forPayment/{subOrderId}")
    public ResponseEntity<SubOderDTOForPayment> forPayment(@PathVariable Long subOrderId) {
        return new ResponseEntity<>(subOrderService.getSubOrderForPayment(subOrderId), HttpStatus.OK);
    }


}
