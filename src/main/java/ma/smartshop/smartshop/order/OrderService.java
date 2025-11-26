package ma.smartshop.smartshop.order;

import lombok.RequiredArgsConstructor;
import ma.smartshop.smartshop.client.ClientService;
import ma.smartshop.smartshop.entity.Client;
import ma.smartshop.smartshop.entity.Order;
import ma.smartshop.smartshop.entity.OrderItem;
import ma.smartshop.smartshop.entity.Product;
import ma.smartshop.smartshop.enums.OrderStatus;
import ma.smartshop.smartshop.order.dto.OrderCreateRequestDto;
import ma.smartshop.smartshop.order.dto.OrderItemRequestDto;
import ma.smartshop.smartshop.order.dto.OrderItemSummaryDto;
import ma.smartshop.smartshop.order.dto.OrderResponseDto;
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

    public OrderResponseDto createOrder(OrderCreateRequestDto dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product id " + product.getId());
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

            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
        }

        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal totalHtAfterDiscount = subTotal.subtract(discount);
        BigDecimal tva = BigDecimal.ZERO;
        BigDecimal totalTtc = totalHtAfterDiscount.add(tva);

        Order order = Order.builder()
                .client(client)
                .createdAt(LocalDateTime.now())
                .subTotalHt(subTotal)
                .discountAmount(discount)
                .totalHtAfterDiscount(totalHtAfterDiscount)
                .tvaAmount(tva)
                .totalTtc(totalTtc)
                .promoCode(dto.getPromoCode())
                .status(OrderStatus.PENDING)
                .remainingAmount(totalTtc)
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
        }
        order.setItems(items);

        Order saved = orderRepository.save(order);

        return toResponseDto(saved);
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
}