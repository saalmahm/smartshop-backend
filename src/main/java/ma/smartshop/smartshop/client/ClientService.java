package ma.smartshop.smartshop.client;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.client.dto.ClientRequestDto;
import ma.smartshop.smartshop.client.dto.ClientResponseDto;
import ma.smartshop.smartshop.client.mapper.ClientMapper;
import ma.smartshop.smartshop.entity.Client;
import ma.smartshop.smartshop.enums.CustomerTier;
import ma.smartshop.smartshop.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientResponseDto createClient(ClientRequestDto dto) {
        Client client = clientMapper.toEntity(dto);
        client.setTier(CustomerTier.BASIC);
        client.setTotalOrders(0);
        client.setTotalSpent(BigDecimal.ZERO);
        return clientMapper.toResponseDto(clientRepository.save(client));
    }

    public ClientResponseDto updateClient(Long id, ClientRequestDto dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        clientMapper.updateEntityFromDto(dto, client);
        return clientMapper.toResponseDto(clientRepository.save(client));
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public ClientResponseDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return clientMapper.toResponseDto(client);
    }

    public List<ClientResponseDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponseDto)
                .toList();
    }
}