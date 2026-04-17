package org.ecomapp.paymentservice.services.refundService;

import org.ecomapp.paymentservice.dtos.request.RefundRequestDTO;
import org.ecomapp.paymentservice.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentservice.enums.RefundStatus;

public interface RefundService {

    RefundResponseDTO getRefund(Long refundId);

    RefundResponseDTO requestRefund(Long userId, Long subOrderId, RefundRequestDTO refundRequestDTO);

    RefundResponseDTO updateRefundStatus(Long refundId, RefundStatus refundStatus);
}
