package ma.smartshop.smartshop.client;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.client.dto.ClientRequestDto;
import ma.smartshop.smartshop.client.dto.ClientResponseDto;
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
}