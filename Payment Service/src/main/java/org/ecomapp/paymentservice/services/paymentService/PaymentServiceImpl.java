package org.ecomapp.paymentservice.services.paymentService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.paymentservice.dtos.clientDTOs.OderDTOForPayment;
import org.ecomapp.paymentservice.dtos.request.PaymentRequestDTO;
import org.ecomapp.paymentservice.dtos.response.PaymentResponseDTO;
import org.ecomapp.paymentservice.enums.PaymentStatus;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.paymentservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.paymentservice.models.Payment;
import org.ecomapp.paymentservice.clients.OrderMSClient;
import org.ecomapp.paymentservice.repositories.PaymentRepository;
import org.ecomapp.paymentservice.services.stripeService.StripeService;
import org.ecomapp.paymentservice.utility.PaymentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMSClient orderMSClient;

    @Transactional
    @Override
    public PaymentResponseDTO initiatePayment(Long userId, Long orderId, PaymentRequestDTO paymentRequestDTO) {
        OderDTOForPayment order = orderMSClient.getOrder(orderId);

        if (!order.getBuyerId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        }
        if (!order.getStatus().equals("PENDING")) {
            throw new ConflictException("Order is not payable");
        }

        boolean activePaymentExists = paymentRepository.existsByOrderIdAndStatusIn(orderId, List.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED));

        if (activePaymentExists) {
            throw new ConflictException("Payment already initiated for this order");
        }

        PaymentIntent paymentIntent = stripeService
                .createPaymentIntent(order.getTotalAmount(), "usd", paymentRequestDTO.getMethod());

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .buyerId(order.getBuyerId())
                .amount(order.getTotalAmount())
                .currency("usd")
                .status(PaymentStatus.PENDING)
                .stripePaymentIntentId(paymentIntent.getId())
                .method(paymentRequestDTO.getMethod())
                .build();

        paymentRepository.save(payment);

        return paymentMapper.paymentToPaymentResDto(payment);
    }

    @Transactional
    @Override
    public void handleWebHook(String payload, String stripeSignature) {

        Event event = stripeService.constructWebhookEvent(payload, stripeSignature);

        if ("payment_intent.succeeded".equals(event.getType())) {

            String paymentIntentId = extractPaymentIntentId(event);

            Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            orderMSClient.changeStatus(payment.getOrderId(), "PAID");

        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            String paymentIntentId = extractPaymentIntentId(event);

            Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }

    @Override
    public PaymentResponseDTO getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).map(paymentMapper::paymentToPaymentResDto)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Override
    public Page<PaymentResponseDTO> getUserPayments(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAllByBuyerId(userId, pageable).map(paymentMapper::paymentToPaymentResDto);
    }

    private String extractPaymentIntentId(Event event) {
        String rawJson = event.getDataObjectDeserializer().getRawJson();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(rawJson);
            return jsonNode.get("id").asText();
        } catch (Exception e) {
            throw new ConflictException("Failed to deserialize payment intent");
        }
    }

}
