package org.ecomapp.paymentservice.dtos.clientDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OderDTOForPayment {
    private Long id;
    private Long buyerId;
    private String status;
    private Double totalAmount;

}
