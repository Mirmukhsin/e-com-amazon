package org.ecomapp.orderservice.dtos.response;

import lombok.*;
import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressSnapshotDTO;
import org.ecomapp.orderservice.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private Long buyerId;
    private AddressSnapshotDTO shippingAddress;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    List<SubOrderResponseDTO> subOrders;
}
