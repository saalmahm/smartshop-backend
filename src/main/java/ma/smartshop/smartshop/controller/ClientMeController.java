package ma.smartshop.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.dto.client.ClientProfileDto;
import ma.smartshop.smartshop.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ma.smartshop.smartshop.dto.order.OrderResponseDto;
import ma.smartshop.smartshop.repository.ClientRepository;
import ma.smartshop.smartshop.exception.ResourceNotFoundException;
import ma.smartshop.smartshop.service.OrderService;
import ma.smartshop.smartshop.entity.Client;
import java.util.List;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class ClientMeController {

    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final OrderService orderService;

    @GetMapping("/profile")
    public ResponseEntity<ClientProfileDto> getMyProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        ClientProfileDto profile = clientService.getProfileForUser(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/orders")
    public List<OrderResponseDto> getMyOrders(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new ResourceNotFoundException("User not authenticated");
        }

        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found for current user"));

        return orderService.getOrdersForClient(client.getId());
    }
}