package ma.smartshop.smartshop.service;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.entity.Order;
import ma.smartshop.smartshop.entity.Payment;
import ma.smartshop.smartshop.enums.OrderStatus;
import ma.smartshop.smartshop.enums.PaymentStatus;
import ma.smartshop.smartshop.enums.PaymentType;
import ma.smartshop.smartshop.dto.payment.PaymentCreateRequestDto;
import ma.smartshop.smartshop.dto.payment.PaymentResponseDto;
import ma.smartshop.smartshop.mapper.PaymentMapper;
import ma.smartshop.smartshop.repository.OrderRepository;
import ma.smartshop.smartshop.repository.PaymentRepository;
import ma.smartshop.smartshop.exception.BusinessValidationException;
import ma.smartshop.smartshop.exception.ResourceNotFoundException;
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

   @Transactional
    public PaymentResponseDto createPayment(PaymentCreateRequestDto dto) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Le montant du paiement doit être positif");
        }

        if (dto.getAmount().compareTo(MAX_PAYMENT_AMOUNT) > 0) {
            throw new BusinessValidationException("Le montant du paiement dépasse la limite légale");
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessValidationException("Les paiements ne peuvent être enregistrés que sur des commandes en attente");
        }

        if (dto.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessValidationException("Le montant du paiement dépasse le montant restant dû");
        }

        List<Payment> existingPayments = paymentRepository.findByOrderOrderByPaymentNumberAsc(order);
        int nextPaymentNumber = existingPayments.size() + 1;

        Payment payment = paymentMapper.toEntity(dto);
        payment.setOrder(order);
        payment.setPaymentNumber(nextPaymentNumber);

        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }

        if (payment.getReference() == null || payment.getReference().isBlank()) {
            String autoRef = "PAY-" + order.getId() + "-" + nextPaymentNumber;
            payment.setReference(autoRef);
        }

        PaymentStatus status;
        LocalDate encashmentDate = null;

        if (dto.getType() == PaymentType.ESPECES) {
            status = PaymentStatus.ENCAISSE;
            encashmentDate = payment.getPaymentDate(); 
        } else {
            status = PaymentStatus.EN_ATTENTE;
        }
        
        payment.setStatus(status);
        payment.setEncashmentDate(encashmentDate);
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        recalculateRemainingAmount(order);

        return paymentMapper.toResponseDto(saved);
    }

    public java.util.List<PaymentResponseDto> getPaymentsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
               .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        java.util.List<Payment> payments = paymentRepository.findByOrderOrderByPaymentNumberAsc(order);
        java.util.List<PaymentResponseDto> dtos = new java.util.ArrayList<>();

        for (Payment payment : payments) {
            dtos.add(paymentMapper.toResponseDto(payment));
        }

        return dtos;
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