package ma.smartshop.smartshop.dto.order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import lombok.Data;

@Data
public class OrderItemRequestDto {

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}