package org.ecomapp.paymentMS.services.refundService;

import org.ecomapp.paymentMS.dtos.request.RefundRequestDTO;
import org.ecomapp.paymentMS.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentMS.enums.RefundStatus;

public interface RefundService {

    RefundResponseDTO getRefund(Long refundId);

    RefundResponseDTO requestRefund(Long subOrderId, RefundRequestDTO refundRequestDTO);

    RefundResponseDTO updateRefundStatus(Long refundId, RefundStatus refundStatus);
}
