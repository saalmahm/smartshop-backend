package ma.smartshop.smartshop.dto.payment;

import lombok.Data;
import ma.smartshop.smartshop.enums.PaymentStatus;
import ma.smartshop.smartshop.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {

    private Long id;
    private Long orderId;
    private Integer paymentNumber;
    private BigDecimal amount;
    private PaymentType type;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private LocalDate encashmentDate;
    private LocalDate dueDate;
    private String reference;
    private String bank;
    private LocalDateTime createdAt;
}