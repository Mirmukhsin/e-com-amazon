package org.ecomapp.orderservice.dtos.clientsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecomapp.orderservice.enums.SubOrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubOderDTOForPayment {
    private Long id;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private SubOrderStatus status;
    private Double subTotal;

}
