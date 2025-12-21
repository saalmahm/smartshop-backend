package ma.smartshop.smartshop.service;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.entity.Client;
import ma.smartshop.smartshop.entity.Order;
import ma.smartshop.smartshop.entity.OrderItem;
import ma.smartshop.smartshop.entity.Product;
import ma.smartshop.smartshop.enums.OrderStatus;
import ma.smartshop.smartshop.dto.order.OrderCreateRequestDto;
import ma.smartshop.smartshop.dto.order.OrderItemRequestDto;
import ma.smartshop.smartshop.dto.order.OrderItemSummaryDto;
import ma.smartshop.smartshop.dto.order.OrderResponseDto;
import ma.smartshop.smartshop.exception.BusinessValidationException;
import ma.smartshop.smartshop.exception.ResourceNotFoundException;
import ma.smartshop.smartshop.repository.ClientRepository;
import ma.smartshop.smartshop.repository.OrderRepository;
import ma.smartshop.smartshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ClientService clientService;
    private final OrderCalculationService orderCalculationService;

    public OrderResponseDto createOrder(OrderCreateRequestDto dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessValidationException("Order must contain at least one item");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        // Contrôle d'usage unique du code promo
        if (dto.getPromoCode() != null && !dto.getPromoCode().isBlank()) {
            if (orderRepository.existsByPromoCode(dto.getPromoCode())) {
                throw new BusinessValidationException("Promo code already used");
            }
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        boolean hasInsufficientStock = false;


        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                hasInsufficientStock = true;
            }

            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();

            items.add(item);
            subTotal = subTotal.add(lineTotal);

            if (!hasInsufficientStock) {
                product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            }
        }

        OrderCalculationService.OrderAmounts amounts = orderCalculationService.calculate(
                subTotal,
                client,
                dto.getPromoCode()
        );
        Order order = Order.builder()
                .client(client)
                .createdAt(LocalDateTime.now())
                .subTotalHt(amounts.subTotalHt())
                .discountAmount(amounts.discountAmount())
                .totalHtAfterDiscount(amounts.totalHtAfterDiscount())
                .tvaAmount(amounts.tvaAmount())
                .totalTtc(amounts.totalTtc())
                .promoCode(dto.getPromoCode())
                .status(hasInsufficientStock ? OrderStatus.REJECTED : OrderStatus.PENDING)
                .remainingAmount(amounts.totalTtc())
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
        }
        order.setItems(items);

        Order saved = orderRepository.save(order);

        return toResponseDto(saved);
    }

    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toResponseDto(order);
    }

    public List<OrderResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponseDto> dtos = new ArrayList<>();
        for (Order order : orders) {
            dtos.add(toResponseDto(order));
        }
        return dtos;
    }

    private OrderResponseDto toResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setClientId(order.getClient().getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setSubTotalHt(order.getSubTotalHt());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalHtAfterDiscount(order.getTotalHtAfterDiscount());
        dto.setTvaAmount(order.getTvaAmount());
        dto.setTotalTtc(order.getTotalTtc());
        dto.setPromoCode(order.getPromoCode());
        dto.setStatus(order.getStatus());
        dto.setRemainingAmount(order.getRemainingAmount());

        List<OrderItemSummaryDto> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemSummaryDto itemDto = new OrderItemSummaryDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setLineTotal(item.getLineTotal());
            items.add(itemDto);
        }
        dto.setItems(items);

        return dto;
    }

    public OrderResponseDto confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessValidationException("Only pending orders can be confirmed");
        }

        if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessValidationException("Order is not fully paid");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        clientService.applyConfirmedOrder(order.getClient().getId(), order.getTotalTtc());

        return toResponseDto(order);
    }

    public OrderResponseDto cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessValidationException("Only pending orders can be canceled");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return toResponseDto(order);
    }

    public OrderResponseDto rejectOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessValidationException("Only pending orders can be rejected");
        }

        order.setStatus(OrderStatus.REJECTED);
        orderRepository.save(order);

        return toResponseDto(order);
    }

    public List<OrderResponseDto> getOrdersForClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        List<Order> orders = orderRepository.findByClientOrderByCreatedAtDesc(client);
        List<OrderResponseDto> dtos = new ArrayList<>();

        for (Order order : orders) {
            dtos.add(toResponseDto(order));
        }

        return dtos;
    }

    //LES 5 premier client effectuer plus cher commande

}
