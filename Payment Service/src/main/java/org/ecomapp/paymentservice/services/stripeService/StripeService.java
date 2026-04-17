package org.ecomapp.paymentservice.services.stripeService;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.BadRequestException;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.paymentservice.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public PaymentIntent createPaymentIntent(Double amount, String currency, PaymentMethod paymentMethod) {
        try {
            String stripeMethod =
                    switch (paymentMethod) {
                        case PAYPAL -> "paypal";
                        case DEBIT_CARD, CREDIT_CARD -> "card";
                    };

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100))
                    .setCurrency(currency)
                    .addPaymentMethodType(stripeMethod)
                    .build();

            return PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new ConflictException("Failed to create payment intent: " + e.getMessage());
        }
    }

    public Event constructWebhookEvent(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Invalid webhook signature: " + e.getMessage());
        }
    }

    public Refund createRefund(String paymentIntentId, Double amount) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount((long) (amount * 100))
                    .build();
            return Refund.create(params);
        } catch (StripeException e) {
            throw new ConflictException("Failed to process refund: " + e.getMessage());
        }
    }
}
