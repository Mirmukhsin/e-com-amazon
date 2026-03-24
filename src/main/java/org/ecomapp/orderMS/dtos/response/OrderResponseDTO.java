package org.ecomapp.orderMS.dtos.response;

import lombok.*;
import org.ecomapp.orderMS.enums.OrderStatus;
import org.ecomapp.userMS.dtos.response.AddressSnapshotDTO;

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
