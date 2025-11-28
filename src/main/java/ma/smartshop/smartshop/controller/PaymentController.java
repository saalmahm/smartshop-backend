package ma.smartshop.smartshop.controller;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.dto.payment.PaymentCreateRequestDto;
import ma.smartshop.smartshop.dto.payment.PaymentResponseDto;
import ma.smartshop.smartshop.service.PaymentService;
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
    @PatchMapping("/{id}/encash")
    public PaymentResponseDto encashPayment(@PathVariable Long id) {
        return paymentService.encashPayment(id);
    }

    @PatchMapping("/{id}/reject")
    public PaymentResponseDto rejectPayment(@PathVariable Long id) {
        return paymentService.rejectPayment(id);
    }
}