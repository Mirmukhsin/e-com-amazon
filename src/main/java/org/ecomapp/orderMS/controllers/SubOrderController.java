package org.ecomapp.orderMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.orderMS.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderMS.enums.SubOrderStatus;
import org.ecomapp.orderMS.services.subOrderService.SubOrderService;
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
    public ResponseEntity<Page<SubOrderResponseDTO>> getSellerSubOrders(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(subOrderService.getSellerSubOrders(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Update sub order status - SELLER only")
    @PatchMapping("/{subOrderId}/status")
    public ResponseEntity<SubOrderResponseDTO> updateSubOrderStatus(@PathVariable Long subOrderId,
                                                                    @RequestBody SubOrderStatus subOrderStatus) {
        return new ResponseEntity<>(subOrderService.updateSubOrderStatus(subOrderId, subOrderStatus), HttpStatus.OK);
    }


}
