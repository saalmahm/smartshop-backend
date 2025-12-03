package ma.smartshop.smartshop.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import ma.smartshop.smartshop.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentCreateRequestDto {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment type is required")
    private PaymentType type;

    private LocalDate paymentDate;
    private LocalDate dueDate;
    private String reference;
    private String bank;
}