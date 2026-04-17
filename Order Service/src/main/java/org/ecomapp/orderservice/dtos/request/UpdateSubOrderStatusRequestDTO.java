package org.ecomapp.orderservice.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.ecomapp.orderservice.enums.SubOrderStatus;

@Getter
@Setter
public class UpdateSubOrderStatusRequestDTO {
    @NotNull
    private SubOrderStatus status;
}
