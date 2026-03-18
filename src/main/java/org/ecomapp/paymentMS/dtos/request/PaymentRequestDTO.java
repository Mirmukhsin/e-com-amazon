package org.ecomapp.paymentMS.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ecomapp.paymentMS.enums.PaymentMethod;

@Getter
@Setter
@ToString
public class PaymentRequestDTO {

    @NotNull
    private PaymentMethod method;
}
