package org.ecomapp.paymentservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ecomapp.paymentservice.enums.PaymentMethod;

@Getter
@Setter
@ToString
public class PaymentRequestDTO {

    @NotNull
    private PaymentMethod method;
}
