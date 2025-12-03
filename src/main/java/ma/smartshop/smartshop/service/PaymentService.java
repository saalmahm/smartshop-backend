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
import java.util.List;


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

        // Numéro de paiement
        List<Payment> existingPayments = paymentRepository.findByOrderOrderByPaymentNumberAsc(order);
        int nextPaymentNumber = existingPayments.size() + 1;

        // Date de paiement (auto si null)
        LocalDate paymentDate = dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now();

        // Référence (auto si null ou vide)
        String reference = (dto.getReference() == null || dto.getReference().isBlank())
                ? "PAY-" + order.getId() + "-" + nextPaymentNumber
                : dto.getReference();

        // Statut et date d'encaissement
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
                .paymentNumber(nextPaymentNumber)
                .amount(dto.getAmount())
                .type(dto.getType())
                .status(status)
                .paymentDate(paymentDate)
                .encashmentDate(encashmentDate)
                .dueDate(dto.getDueDate())
                .reference(reference)
                .bank(dto.getBank())
                .createdAt(LocalDateTime.now())
                .build();

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

    public PaymentResponseDto encashPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        if (payment.getStatus() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessValidationException("Seuls les paiements en attente peuvent être encaissés");
        }

        payment.setStatus(PaymentStatus.ENCAISSE);
        payment.setEncashmentDate(LocalDate.now());

        Payment saved = paymentRepository.save(payment);

        // Recalcul du montant restant de la commande
        recalculateRemainingAmount(payment.getOrder());

        return paymentMapper.toResponseDto(saved);
    }

    public PaymentResponseDto rejectPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        if (payment.getStatus() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessValidationException("Seuls les paiements en attente peuvent être rejetés");
        }

        payment.setStatus(PaymentStatus.REJETE);
        payment.setEncashmentDate(null);

        Payment saved = paymentRepository.save(payment);

        recalculateRemainingAmount(payment.getOrder());

        return paymentMapper.toResponseDto(saved);
    }
    public List<Order> partial(){
        return paymentRepository.findAll().stream()
                .filter(p -> p.getType() == PaymentType.ESPECES && p.getType() == PaymentType.CHEQUE
                        && p.getType() == PaymentType.VIREMENT)
                .map(Payment::getOrder)
                .toList();
    }
}