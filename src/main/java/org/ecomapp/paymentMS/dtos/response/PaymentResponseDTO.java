package org.ecomapp.paymentMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ecomapp.paymentMS.enums.PaymentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private Long buyerId;
    private Double amount;
    private String currency;
    private String method;
    private PaymentStatus status;
    private String stripePaymentIntentId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
