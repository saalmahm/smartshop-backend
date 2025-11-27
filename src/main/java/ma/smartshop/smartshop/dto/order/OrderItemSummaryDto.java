package ma.smartshop.smartshop.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemSummaryDto {

    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}