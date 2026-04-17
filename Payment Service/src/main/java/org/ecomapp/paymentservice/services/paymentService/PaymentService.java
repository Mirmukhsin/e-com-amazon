package org.ecomapp.paymentservice.services.paymentService;

import org.ecomapp.paymentservice.dtos.request.PaymentRequestDTO;
import org.ecomapp.paymentservice.dtos.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;

public interface PaymentService {
    PaymentResponseDTO initiatePayment(Long userId,Long orderId, PaymentRequestDTO paymentRequestDTO);

    void handleWebHook(String payload, String stripeSignature);

    PaymentResponseDTO getPayment(Long paymentId);

    Page<PaymentResponseDTO> getUserPayments(Long userId, int page, int size);
}
