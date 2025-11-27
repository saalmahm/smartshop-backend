package ma.smartshop.smartshop.payment;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.payment.dto.PaymentCreateRequestDto;
import ma.smartshop.smartshop.payment.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentCreateRequestDto dto) {
        PaymentResponseDto response = paymentService.createPayment(dto);
        return ResponseEntity.ok(response);
    }
}