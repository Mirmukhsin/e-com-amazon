package org.ecomapp.paymentservice.utility;

import org.ecomapp.paymentservice.dtos.response.PaymentResponseDTO;
import org.ecomapp.paymentservice.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentservice.models.Payment;
import org.ecomapp.paymentservice.models.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "buyerId", source = "buyerId")
    @Mapping(target = "orderId", source = "orderId")
    PaymentResponseDTO paymentToPaymentResDto(Payment payment);

    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "subOrderId", source = "subOrderId")
    RefundResponseDTO refundToRefundResDto(Refund refund);
}
