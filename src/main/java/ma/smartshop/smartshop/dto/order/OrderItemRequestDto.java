package ma.smartshop.smartshop.dto.order;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;
}