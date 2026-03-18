package org.ecomapp.orderMS.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDTO {

    @NotNull
    private Long cartId;

    @NotNull
    private Long addressId;
}
