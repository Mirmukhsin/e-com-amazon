package org.ecomapp.orderMS.dtos.response;

import lombok.*;
import org.ecomapp.orderMS.enums.SubOrderStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubOrderResponseDTO {

    private Long id;
    private Long orderId;
    private Long sellerId;
    private String sellerStoreName;
    private Double subTotal;
    private SubOrderStatus status;

    List<OrderItemResponseDTO> items;
}
