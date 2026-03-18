package org.ecomapp.paymentMS.utility;

import org.ecomapp.paymentMS.dtos.response.PaymentResponseDTO;
import org.ecomapp.paymentMS.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentMS.models.Payment;
import org.ecomapp.paymentMS.models.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "orderId", source = "order.id")
    PaymentResponseDTO paymentToPaymentResDto(Payment payment);

    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "subOrderId", source = "subOrder.id")
    RefundResponseDTO refundToRefundResDto(Refund refund);
}
