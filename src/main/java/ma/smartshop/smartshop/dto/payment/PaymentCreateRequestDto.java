package ma.smartshop.smartshop.dto.payment;

import lombok.Data;
import ma.smartshop.smartshop.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentCreateRequestDto {

    private Long orderId;
    private BigDecimal amount;
    private PaymentType type;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private String reference;
    private String bank;
}