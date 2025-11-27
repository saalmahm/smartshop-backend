package ma.smartshop.smartshop.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequestDto {
    private Long clientId;
    private String promoCode;
    private List<OrderItemRequestDto> items;
}