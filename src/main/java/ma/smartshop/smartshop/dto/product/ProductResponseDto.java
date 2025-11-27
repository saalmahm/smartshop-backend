package ma.smartshop.smartshop.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
}