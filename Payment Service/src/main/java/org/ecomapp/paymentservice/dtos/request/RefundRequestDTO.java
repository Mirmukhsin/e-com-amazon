package org.ecomapp.paymentservice.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequestDTO {
    @NotBlank
    private String reason;

}
