package org.ecomapp.paymentMS.services.paymentService;

import org.ecomapp.paymentMS.dtos.request.PaymentRequestDTO;
import org.ecomapp.paymentMS.dtos.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;

public interface PaymentService {
    PaymentResponseDTO initiatePayment(Long orderId, PaymentRequestDTO paymentRequestDTO);

    void handleWebHook(String payload, String stripeSignature);

    PaymentResponseDTO getPayment(Long paymentId);

    Page<PaymentResponseDTO> getUserPayments(int page, int size);
}
