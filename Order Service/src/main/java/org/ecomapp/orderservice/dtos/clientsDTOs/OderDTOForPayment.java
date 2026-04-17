package org.ecomapp.orderservice.dtos.clientsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecomapp.orderservice.enums.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OderDTOForPayment {
    private Long id;
    private Long buyerId;
    private OrderStatus status;
    private Double totalAmount;

}
