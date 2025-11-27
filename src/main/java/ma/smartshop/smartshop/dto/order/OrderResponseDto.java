package ma.smartshop.smartshop.dto.order;

import lombok.Data;
import ma.smartshop.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {

    private Long id;
    private Long clientId;
    private LocalDateTime createdAt;
    private BigDecimal subTotalHt;
    private BigDecimal discountAmount;
    private BigDecimal totalHtAfterDiscount;
    private BigDecimal tvaAmount;
    private BigDecimal totalTtc;
    private String promoCode;
    private OrderStatus status;
    private BigDecimal remainingAmount;
    private List<OrderItemSummaryDto> items;
}