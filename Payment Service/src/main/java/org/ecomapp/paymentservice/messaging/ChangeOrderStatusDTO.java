package org.ecomapp.paymentservice.messaging;

public record ChangeOrderStatusDTO(Long orderId, String orderStatus) {
}
