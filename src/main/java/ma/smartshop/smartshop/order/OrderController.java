package ma.smartshop.smartshop.order;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.order.dto.OrderCreateRequestDto;
import ma.smartshop.smartshop.order.dto.OrderResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateRequestDto dto) {
        OrderResponseDto response = orderService.createOrder(dto);
        return ResponseEntity.ok(response);
    }
}