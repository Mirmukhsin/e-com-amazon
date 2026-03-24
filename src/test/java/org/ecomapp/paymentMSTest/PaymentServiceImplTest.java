package org.ecomapp.paymentMSTest;

import com.stripe.model.PaymentIntent;
import org.ecomapp.exceptionHandling.customExceptions.ConflictException;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.orderMS.enums.OrderStatus;
import org.ecomapp.orderMS.models.Order;
import org.ecomapp.orderMS.repositories.OrderRepository;
import org.ecomapp.paymentMS.dtos.request.PaymentRequestDTO;
import org.ecomapp.paymentMS.dtos.response.PaymentResponseDTO;
import org.ecomapp.paymentMS.enums.PaymentMethod;
import org.ecomapp.paymentMS.enums.PaymentStatus;
import org.ecomapp.paymentMS.models.Payment;
import org.ecomapp.paymentMS.repositories.PaymentRepository;
import org.ecomapp.paymentMS.services.paymentService.PaymentServiceImpl;
import org.ecomapp.paymentMS.services.stripeService.StripeService;
import org.ecomapp.paymentMS.utility.PaymentMapper;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private StripeService stripeService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User buyer;
    private Order order;
    private Payment payment;
    private PaymentResponseDTO paymentResponseDTO;
    private PaymentIntent paymentIntent;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .id(1L)
                .email("buyer@gmail.com")
                .build();

        order = Order.builder()
                .id(1L)
                .buyer(buyer)
                .totalAmount(2500.0)
                .status(OrderStatus.PENDING)
                .build();

        payment = Payment.builder()
                .id(1L)
                .order(order)
                .buyer(buyer)
                .amount(2500.0)
                .currency("usd")
                .method(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .stripePaymentIntentId("pi_test_123")
                .build();

        paymentResponseDTO = PaymentResponseDTO.builder()
                .id(1L)
                .orderId(1L)
                .amount(2500.0)
                .status(PaymentStatus.PENDING)
                .stripePaymentIntentId("pi_test_123")
                .build();

        paymentIntent = mock(PaymentIntent.class);
    }

    // -----------------------------------------
    // INITIATE PAYMENT TESTS
    // -----------------------------------------

    @Test
    void initiatePayment_shouldCreatePayment_whenValid() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setMethod(PaymentMethod.CREDIT_CARD);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrder_IdAndStatusIn(1L, List.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED))).thenReturn(false);
        when(stripeService.createPaymentIntent(2500.0, "usd", PaymentMethod.CREDIT_CARD)).thenReturn(paymentIntent);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.paymentToPaymentResDto(any(Payment.class))).thenReturn(paymentResponseDTO);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        PaymentResponseDTO result = paymentService.initiatePayment(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getStripePaymentIntentId()).isEqualTo("pi_test_123");
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void initiatePayment_shouldThrowException_whenOrderNotFound() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setMethod(PaymentMethod.CREDIT_CARD);

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.initiatePayment(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void initiatePayment_shouldThrowException_whenOrderNotBelongToUser() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setMethod(PaymentMethod.CREDIT_CARD);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, dto))
                .isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    void initiatePayment_shouldThrowException_whenOrderNotPending() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setMethod(PaymentMethod.CREDIT_CARD);

        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("not payable");
    }

    @Test
    void initiatePayment_shouldThrowException_whenPaymentAlreadyExists() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setMethod(PaymentMethod.CREDIT_CARD);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.existsByOrder_IdAndStatusIn(1L, List.of(PaymentStatus.PENDING, PaymentStatus.COMPLETED))).thenReturn(true);

        when(securityUtils.getCurrentUserId()).thenReturn(1L);

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already initiated");
    }

    // ─────────────────────────────────────────
    // GET PAYMENT TESTS
    // ─────────────────────────────────────────

    @Test
    void getPayment_shouldReturnPayment_whenFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.paymentToPaymentResDto(payment)).thenReturn(paymentResponseDTO);

        PaymentResponseDTO result = paymentService.getPayment(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getPayment_shouldThrowException_whenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
