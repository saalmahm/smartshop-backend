package ma.smartshop.smartshop.client;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.client.dto.ClientProfileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class ClientMeController {

    private final ClientService clientService;

    @GetMapping("/profile")
    public ResponseEntity<ClientProfileDto> getMyProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        ClientProfileDto profile = clientService.getProfileForUser(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Object>> getMyOrders(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        List<Object> orders = clientService.getOrderHistoryForUser(userId);
        return ResponseEntity.ok(orders);
    }
}