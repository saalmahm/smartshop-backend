package ma.smartshop.smartshop.service;

import ma.smartshop.smartshop.dto.payment.PaymentCreateRequestDto;
import ma.smartshop.smartshop.dto.payment.PaymentResponseDto;
import ma.smartshop.smartshop.entity.Order;
import ma.smartshop.smartshop.entity.Payment;
import ma.smartshop.smartshop.enums.PaymentStatus;
import ma.smartshop.smartshop.enums.PaymentType;
import ma.smartshop.smartshop.enums.OrderStatus;
import ma.smartshop.smartshop.exception.BusinessValidationException;
import ma.smartshop.smartshop.mapper.PaymentMapper;
import ma.smartshop.smartshop.repository.OrderRepository;
import ma.smartshop.smartshop.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void test_createPayment_updates_remainingAmount_for_cash() {
        // Given
        Order order = new Order();
        order.setId(1L);
        order.setTotalTtc(new BigDecimal("1000.00"));
        order.setRemainingAmount(new BigDecimal("1000.00"));
        order.setStatus(OrderStatus.PENDING);  // IMPORTANT pour passer la règle du service

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        PaymentCreateRequestDto dto = new PaymentCreateRequestDto();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("400.00"));
        dto.setType(PaymentType.ESPECES);
        dto.setPaymentDate(LocalDate.now());

        // le service commence par chercher les paiements existants pour le numéro
        when(paymentRepository.findByOrderOrderByPaymentNumberAsc(order))
                .thenReturn(Collections.emptyList());

        // stub du save pour renvoyer un Payment avec amount = 400 et status = ENCAISSE
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        // quand on recalcule le remainingAmount, on veut que le repo retourne le paiement encaissé de 400
        when(paymentRepository.findByOrderAndStatus(order, PaymentStatus.ENCAISSE))
                .thenAnswer(invocation -> {
                    Payment p = new Payment();
                    p.setOrder(order);
                    p.setAmount(new BigDecimal("400.00"));
                    p.setStatus(PaymentStatus.ENCAISSE);
                    return List.of(p);
                });

        // mapper
        when(paymentMapper.toResponseDto(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            PaymentResponseDto resp = new PaymentResponseDto();
            resp.setId(p.getId());
            resp.setOrderId(p.getOrder().getId());
            resp.setAmount(p.getAmount());
            resp.setStatus(p.getStatus());
            return resp;
        });

        // When
        PaymentResponseDto response = paymentService.createPayment(dto);

        // Then
        assertEquals(new BigDecimal("600.00"), order.getRemainingAmount());
        assertEquals(PaymentStatus.ENCAISSE, response.getStatus());
        assertEquals(new BigDecimal("400.00"), response.getAmount());
    }

    @Test
    void test_createPayment_amount_too_big_throws() {
        // Given
        PaymentCreateRequestDto dto = new PaymentCreateRequestDto();
        dto.setOrderId(1L);
        dto.setAmount(new BigDecimal("200000.00"));   // > MAX_PAYMENT_AMOUNT = 20000
        dto.setType(PaymentType.ESPECES);

        // When / Then
        assertThrows(BusinessValidationException.class,
                () -> paymentService.createPayment(dto));
    }
}
