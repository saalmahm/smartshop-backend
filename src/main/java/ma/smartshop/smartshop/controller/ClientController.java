package ma.smartshop.smartshop.controller;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.client.dto.ClientRequestDto;
import ma.smartshop.smartshop.client.dto.ClientResponseDto;
import ma.smartshop.smartshop.dto.client.ClientUserCreateRequestDto;
import ma.smartshop.smartshop.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.createClient(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable Long id,
                                                          @RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PostMapping("/{id}/user")
    public ResponseEntity<ClientResponseDto> createClientUser(@PathVariable Long id,
                                                              @RequestBody ClientUserCreateRequestDto dto) {
        return ResponseEntity.ok(clientService.createUserForClient(id, dto));
    }
}