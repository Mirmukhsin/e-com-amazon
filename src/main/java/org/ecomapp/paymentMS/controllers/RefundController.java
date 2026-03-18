package org.ecomapp.paymentMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.paymentMS.dtos.request.RefundRequestDTO;
import org.ecomapp.paymentMS.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentMS.enums.RefundStatus;
import org.ecomapp.paymentMS.services.refundService.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
@Tag(name = "H Refunds", description = "Refund management")
public class RefundController {
    private final RefundService refundService;

    @Operation(summary = "Get refund details")
    @GetMapping("/{refundId}")
    public ResponseEntity<RefundResponseDTO> getRefund(@PathVariable Long refundId) {
        return new ResponseEntity<>(refundService.getRefund(refundId), HttpStatus.OK);
    }

    @Operation(summary = "Send request for  refund")
    @PostMapping("/sub-orders/{subOrderId}")
    public ResponseEntity<RefundResponseDTO> requestRefund(@PathVariable Long subOrderId,
                                                           @Valid @RequestBody RefundRequestDTO refundRequestDTO) {

        return new ResponseEntity<>(refundService.requestRefund(subOrderId, refundRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update refund status - ADMIN only")
    @PatchMapping("/{refundId}/status")
    public ResponseEntity<RefundResponseDTO> updateRefundStatus(@PathVariable Long refundId,
                                                                @RequestBody RefundStatus refundStatus) {

        return new ResponseEntity<>(refundService.updateRefundStatus(refundId, refundStatus), HttpStatus.OK);
    }
}
