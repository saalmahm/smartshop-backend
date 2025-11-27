package ma.smartshop.smartshop.client.dto;

import lombok.Data;
import ma.smartshop.smartshop.enums.CustomerTier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private CustomerTier tier;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
}