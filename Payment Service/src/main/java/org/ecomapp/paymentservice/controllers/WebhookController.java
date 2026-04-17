package org.ecomapp.paymentservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.paymentservice.services.paymentService.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Tag(name = "G. Webhooks", description = "Stripe webhook handling")
public class WebhookController {
    private final PaymentService paymentService;

    @Operation(summary = "Webhook signature")
    @PostMapping("/stripe")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload,
                                              @RequestHeader("Stripe-Signature") String stripeSignature) {

        paymentService.handleWebHook(payload, stripeSignature);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
