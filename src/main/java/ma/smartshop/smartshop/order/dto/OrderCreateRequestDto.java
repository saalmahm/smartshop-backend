package ma.smartshop.smartshop.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequestDto {
    private Long clientId;
    private String promoCode;
    private List<OrderItemRequestDto> items;
}