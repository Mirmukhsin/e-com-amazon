package org.ecomapp.paymentMS.services.refundService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderMS.enums.SubOrderStatus;
import org.ecomapp.orderMS.models.SubOrder;
import org.ecomapp.orderMS.repositories.SubOrderRepository;
import org.ecomapp.paymentMS.dtos.request.RefundRequestDTO;
import org.ecomapp.paymentMS.dtos.response.RefundResponseDTO;
import org.ecomapp.paymentMS.enums.PaymentStatus;
import org.ecomapp.paymentMS.enums.RefundStatus;
import org.ecomapp.paymentMS.models.Payment;
import org.ecomapp.paymentMS.models.Refund;
import org.ecomapp.paymentMS.repositories.PaymentRepository;
import org.ecomapp.paymentMS.repositories.RefundRepository;
import org.ecomapp.paymentMS.services.stripeService.StripeService;
import org.ecomapp.paymentMS.utility.PaymentMapper;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {
    private final RefundRepository refundRepository;
    private final SubOrderRepository subOrderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final SecurityUtils securityUtils;

    private final Long CURRENT_USER_ID = securityUtils.getCurrentUserId();


    @Override
    public RefundResponseDTO getRefund(Long refundId) {
        return refundRepository.findById(refundId).map(paymentMapper::refundToRefundResDto)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));
    }

    @Override
    public RefundResponseDTO requestRefund(Long subOrderId, RefundRequestDTO refundRequestDTO) {
        SubOrder subOrder = subOrderRepository.findById(subOrderId).orElseThrow(() -> new ResourceNotFoundException("Sub order not found"));
        if (!subOrder.getOrder().getBuyer().getId().equals(CURRENT_USER_ID)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        if (!subOrder.getStatus().equals(SubOrderStatus.DELIVERED)) {
            throw new ConflictException("Can only refund delivered orders");
        }

        boolean alreadyRequested = refundRepository.existsBySubOrder_Id(subOrderId);
        if (alreadyRequested) {
            throw new ConflictException("Refund already requested for this order");
        }

        Payment payment = paymentRepository.findByOrder_Id(subOrder.getOrder().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        Refund refund = Refund.builder()
                .subOrder(subOrder)
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
