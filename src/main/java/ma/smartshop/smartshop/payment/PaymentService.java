package ma.smartshop.smartshop.payment;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.entity.Order;
import ma.smartshop.smartshop.entity.Payment;
import ma.smartshop.smartshop.enums.OrderStatus;
import ma.smartshop.smartshop.enums.PaymentStatus;
import ma.smartshop.smartshop.enums.PaymentType;
import ma.smartshop.smartshop.payment.dto.PaymentCreateRequestDto;
import ma.smartshop.smartshop.payment.dto.PaymentResponseDto;
import ma.smartshop.smartshop.payment.mapper.PaymentMapper;
import ma.smartshop.smartshop.repository.OrderRepository;
import ma.smartshop.smartshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private static final BigDecimal MAX_PAYMENT_AMOUNT = new BigDecimal("20000");

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    public PaymentResponseDto createPayment(PaymentCreateRequestDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be positive");
        }

        if (dto.getAmount().compareTo(MAX_PAYMENT_AMOUNT) > 0) {
            throw new RuntimeException("Payment amount exceeds legal limit");
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Payments can only be registered on pending orders");
        }

        if (dto.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new RuntimeException("Payment amount exceeds remaining amount");
        }

        int nextNumber = paymentRepository.countByOrder(order) + 1;

        LocalDate paymentDate = dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now();

        PaymentStatus status;
        LocalDate encashmentDate = null;

        if (dto.getType() == PaymentType.ESPECES) {
            status = PaymentStatus.ENCAISSE;
            encashmentDate = paymentDate;
        } else {
            status = PaymentStatus.EN_ATTENTE;
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentNumber(nextNumber)
                .amount(dto.getAmount())
                .type(dto.getType())
                .status(status)
                .paymentDate(paymentDate)
                .encashmentDate(encashmentDate)
                .dueDate(dto.getDueDate())
                .reference(dto.getReference())
                .bank(dto.getBank())
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        recalculateRemainingAmount(order);

        return paymentMapper.toResponseDto(saved);
    }

    private void recalculateRemainingAmount(Order order) {
        java.util.List<Payment> payments = paymentRepository.findByOrderAndStatus(order, PaymentStatus.ENCAISSE);

        BigDecimal totalPaid = BigDecimal.ZERO;
        for (Payment p : payments) {
            totalPaid = totalPaid.add(p.getAmount());
        }

        BigDecimal newRemaining = order.getTotalTtc().subtract(totalPaid);
        if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
            newRemaining = BigDecimal.ZERO;
        }

        order.setRemainingAmount(newRemaining);
        orderRepository.save(order);
    }
}