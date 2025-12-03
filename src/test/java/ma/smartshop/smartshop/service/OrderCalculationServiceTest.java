package ma.smartshop.smartshop.service;

import ma.smartshop.smartshop.entity.Client;
import ma.smartshop.smartshop.enums.CustomerTier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderCalculationServiceTest {

    private final OrderCalculationService service = new OrderCalculationService();

    @Test
    void test_calculate_with_silver_and_promo() {
        // Given
        BigDecimal subTotalHt = new BigDecimal("1000.00");
        Client client = new Client();
        client.setTier(CustomerTier.SILVER);
        String promoCode = "PROMO-AB12";

        // When
        OrderCalculationService.OrderAmounts result =
                service.calculate(subTotalHt, client, promoCode);

        // Then
        assertEquals(new BigDecimal("1000.00"), result.subTotalHt());
        assertEquals(new BigDecimal("100.00"), result.discountAmount());          // 50 fidélité + 50 promo
        assertEquals(new BigDecimal("900.00"), result.totalHtAfterDiscount());
        assertEquals(new BigDecimal("180.00"), result.tvaAmount());               // 20% sur 900
        assertEquals(new BigDecimal("1080.00"), result.totalTtc());
    }

    @Test
    void test_calculate_without_promo_and_basic() {
        // Given
        BigDecimal subTotalHt = new BigDecimal("300.00");
        Client client = new Client();
        client.setTier(CustomerTier.BASIC);
        String promoCode = null;

        // When
        OrderCalculationService.OrderAmounts result =
                service.calculate(subTotalHt, client, promoCode);

        // Then
        assertEquals(new BigDecimal("300.00"), result.subTotalHt());
        assertEquals(new BigDecimal("0.00"), result.discountAmount());
        assertEquals(new BigDecimal("300.00"), result.totalHtAfterDiscount());
        assertEquals(new BigDecimal("60.00"), result.tvaAmount());               // 20% sur 300
        assertEquals(new BigDecimal("360.00"), result.totalTtc());
    }
}