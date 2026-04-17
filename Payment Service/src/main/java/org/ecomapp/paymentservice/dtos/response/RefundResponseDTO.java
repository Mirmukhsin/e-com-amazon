package org.ecomapp.paymentservice.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ecomapp.paymentservice.enums.RefundStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RefundResponseDTO {

    private Long id;
    private Long paymentId;
    private Long subOrderId;
    private Double amount;
    private String reason;
    private RefundStatus status;
    private LocalDateTime createdAt;
}
