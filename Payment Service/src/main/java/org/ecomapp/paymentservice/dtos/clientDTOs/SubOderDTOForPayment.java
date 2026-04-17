package org.ecomapp.paymentservice.dtos.clientDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubOderDTOForPayment {
    private Long id;
    private Long orderId;
    private Long buyerId;
    private Long sellerId;
    private String status;
    private Double subTotal;

}
