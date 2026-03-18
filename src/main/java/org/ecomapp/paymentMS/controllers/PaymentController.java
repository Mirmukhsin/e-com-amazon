package org.ecomapp.paymentMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.paymentMS.dtos.request.PaymentRequestDTO;
import org.ecomapp.paymentMS.dtos.response.PaymentResponseDTO;
import org.ecomapp.paymentMS.services.paymentService.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "F. Payments", description = "Payment processing")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Get payment details")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable Long paymentId) {
        return new ResponseEntity<>(paymentService.getPayment(paymentId), HttpStatus.OK);
    }

    @Operation(summary = "Get user payments")
    @GetMapping("/me")
    public ResponseEntity<Page<PaymentResponseDTO>> getUserPayments(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {

        return new ResponseEntity<>(paymentService.getUserPayments(page, size), HttpStatus.OK);
    }

    @Operation(summary = "Create payment")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment failed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    }
    )
    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@PathVariable Long orderId,
                                                              @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {

        return new ResponseEntity<>(paymentService.initiatePayment(orderId, paymentRequestDTO), HttpStatus.CREATED);
    }
}
