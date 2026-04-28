package org.ecomapp.orderservice.messaging;

public record ChangeOrderStatusDTO(Long orderId, String orderStatus) {
}
