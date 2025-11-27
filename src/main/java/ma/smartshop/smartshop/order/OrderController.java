package ma.smartshop.smartshop.order;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.order.dto.OrderCreateRequestDto;
import ma.smartshop.smartshop.order.dto.OrderResponseDto;
import ma.smartshop.smartshop.payment.PaymentService;
import ma.smartshop.smartshop.payment.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateRequestDto dto) {
        OrderResponseDto response = orderService.createOrder(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDto> confirmOrder(@PathVariable Long id) {
        OrderResponseDto order = orderService.confirmOrder(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id) {
        OrderResponseDto order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<OrderResponseDto> rejectOrder(@PathVariable Long id) {
        OrderResponseDto order = orderService.rejectOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<java.util.List<PaymentResponseDto>> getOrderPayments(@PathVariable Long id) {
        java.util.List<PaymentResponseDto> payments = paymentService.getPaymentsByOrderId(id);
        return ResponseEntity.ok(payments);
    }
}