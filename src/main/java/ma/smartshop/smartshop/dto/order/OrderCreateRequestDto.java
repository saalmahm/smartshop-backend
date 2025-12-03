package ma.smartshop.smartshop.dto.order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Data;

@Data
public class OrderCreateRequestDto {

    @NotNull(message = "Client id is required")
    private Long clientId;

    @Pattern(
        regexp = "PROMO-[A-Z0-9]{4}",
        message = "Promo code must match pattern PROMO-XXXX",
        groups = {}
    )
    private String promoCode;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequestDto> items;
}