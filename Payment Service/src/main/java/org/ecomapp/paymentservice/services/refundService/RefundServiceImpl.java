package org.ecomapp.paymentservice.services.refundService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.paymentservice.dtos.clientDTOs.SubOderDTOForPayment;
import org.ecomapp.paymentservice.dtos.request.RefundRequestDTO;
import org.ecomapp.paymentservice.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentservice.enums.PaymentStatus;
import org.ecomapp.paymentservice.enums.RefundStatus;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.paymentservice.models.Payment;
import org.ecomapp.paymentservice.models.Refund;
import org.ecomapp.paymentservice.clients.OrderMSClient;
import org.ecomapp.paymentservice.repositories.PaymentRepository;
import org.ecomapp.paymentservice.repositories.RefundRepository;
import org.ecomapp.paymentservice.services.stripeService.StripeService;
import org.ecomapp.paymentservice.utility.PaymentMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final OrderMSClient orderMSClient;


    @Override
    public RefundResponseDTO getRefund(Long refundId) {
        return refundRepository.findById(refundId).map(paymentMapper::refundToRefundResDto)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));
    }

    @Override
    public RefundResponseDTO requestRefund(Long userId, Long subOrderId, RefundRequestDTO refundRequestDTO) {
        SubOderDTOForPayment subOrder = orderMSClient.getSubOrder(subOrderId);
        if (!subOrder.getBuyerId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        if (!subOrder.getStatus().equals("DELIVERED")) {
            throw new ConflictException("Can only refund delivered orders");
        }

        boolean alreadyRequested = refundRepository.existsBySubOrderId(subOrderId);
        if (alreadyRequested) {
            throw new ConflictException("Refund already requested for this order");
        }

        Payment payment = paymentRepository.findByOrderId(subOrder.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Refund refund = Refund.builder()
                .subOrderId(subOrderId)
                .amount(subOrder.getSubTotal())
                .reason(refundRequestDTO.getReason())
                .status(RefundStatus.PENDING)
                .payment(payment)
                .build();
        refundRepository.save(refund);

        return paymentMapper.refundToRefundResDto(refund);
    }

    @Transactional
    @Override
    public RefundResponseDTO updateRefundStatus(Long refundId, RefundStatus refundStatus) {

        Refund refund = refundRepository.findById(refundId).orElseThrow(() -> new ResourceNotFoundException("Refund not found"));

        if (!refund.getStatus().equals(RefundStatus.PENDING)) {
            throw new ConflictException("Refund is not acceptable");
        }
        if (refundStatus.equals(RefundStatus.APPROVED)) {
            stripeService.createRefund(refund.getPayment().getStripePaymentIntentId(), refund.getAmount());

            Payment payment = refund.getPayment();

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            refund.setStatus(RefundStatus.APPROVED);
        }
        if (refundStatus.equals(RefundStatus.REJECTED)) {
            refund.setStatus(RefundStatus.REJECTED);
        }
        refundRepository.save(refund);

        return paymentMapper.refundToRefundResDto(refund);
    }
}
