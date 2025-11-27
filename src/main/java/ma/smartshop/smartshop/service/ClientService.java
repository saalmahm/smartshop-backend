package ma.smartshop.smartshop.service;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.client.dto.ClientProfileDto;
import ma.smartshop.smartshop.client.dto.ClientRequestDto;
import ma.smartshop.smartshop.client.dto.ClientResponseDto;
import ma.smartshop.smartshop.client.mapper.ClientMapper;
import ma.smartshop.smartshop.entity.Client;
import ma.smartshop.smartshop.enums.CustomerTier;
import ma.smartshop.smartshop.repository.ClientRepository;
import ma.smartshop.smartshop.dto.client.ClientUserCreateRequestDto;
import ma.smartshop.smartshop.entity.User;
import ma.smartshop.smartshop.enums.UserRole;
import ma.smartshop.smartshop.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final LoyaltyService loyaltyService;
    private final UserRepository userRepository;

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

    public ClientProfileDto getProfileForUser(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client not found for user " + userId));
        return clientMapper.toProfileDto(client);
    }

    public List<Object> getOrderHistoryForUser(Long userId) {
    // TODO: à implémenter quand l'entité Order sera créée
    return List.of();
    }

    public void applyConfirmedOrder(Long clientId, BigDecimal orderTotal) {
    Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

    client.setTotalOrders(client.getTotalOrders() + 1);
    client.setTotalSpent(client.getTotalSpent().add(orderTotal));

    LocalDateTime now = LocalDateTime.now();
    if (client.getFirstOrderDate() == null) {
        client.setFirstOrderDate(now);
    }
    client.setLastOrderDate(now);

    CustomerTier newTier = loyaltyService.calculateTier(
            client.getTotalOrders(),
            client.getTotalSpent()
    );
    client.setTier(newTier);

    clientRepository.save(client);
}

    public ClientResponseDto createUserForClient(Long clientId, ClientUserCreateRequestDto dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (client.getUser() != null) {
            throw new RuntimeException("Client already has a user account");
        }

        userRepository.findByUsername(dto.getUsername())
                .ifPresent(u -> {
                    throw new RuntimeException("Username already exists");
                });

        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(UserRole.CLIENT)
                .build();

        User savedUser = userRepository.save(user);

        client.setUser(savedUser);
        Client savedClient = clientRepository.save(client);

        return clientMapper.toResponseDto(savedClient);
    }
}