package org.ecomapp.paymentMS.services.paymentService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderMS.enums.OrderStatus;
import org.ecomapp.orderMS.models.Order;
import org.ecomapp.orderMS.repositories.OrderRepository;
import org.ecomapp.paymentMS.dtos.request.PaymentRequestDTO;
import org.ecomapp.paymentMS.dtos.response.PaymentResponseDTO;
import org.ecomapp.paymentMS.enums.PaymentStatus;
import org.ecomapp.paymentMS.models.Payment;
import org.ecomapp.paymentMS.repositories.PaymentRepository;
import org.ecomapp.paymentMS.services.stripeService.StripeService;
import org.ecomapp.paymentMS.utility.PaymentMapper;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final SecurityUtils securityUtils;

    private final Long CURRENT_USER_ID = securityUtils.getCurrentUserId();

    @Transactional
    @Override
    public PaymentResponseDTO initiatePayment(Long orderId, PaymentRequestDTO paymentRequestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getBuyer().getId().equals(CURRENT_USER_ID)) {
            throw new UnAuthorizedException("Unauthorized");
        }
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new ConflictException("Order is not payable");
        }

        boolean activePaymentExists = paymentRepository.existsByOrder_IdAndStatusIn(orderId, List.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED));

        if (activePaymentExists) {
            throw new ConflictException("Payment already initiated for this order");
        }

        PaymentIntent paymentIntent = stripeService
                .createPaymentIntent(order.getTotalAmount(), "usd", paymentRequestDTO.getMethod());

        Payment payment = Payment.builder()
                .order(order)
                .buyer(order.getBuyer())
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

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
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
    public Page<PaymentResponseDTO> getUserPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAllByBuyer_Id(CURRENT_USER_ID, pageable).map(paymentMapper::paymentToPaymentResDto);
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
