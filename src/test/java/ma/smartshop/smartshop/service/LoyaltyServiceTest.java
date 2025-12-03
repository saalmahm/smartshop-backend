package ma.smartshop.smartshop.service;

import ma.smartshop.smartshop.enums.CustomerTier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyServiceTest {

    private final LoyaltyService service = new LoyaltyService();

    @Test
    void test_tier_basic_to_silver_by_orders() {
        // 3 commandes, 500 DH cumulés
        CustomerTier tier = service.calculateTier(
                3,
                new BigDecimal("500.00")
        );

        assertEquals(CustomerTier.SILVER, tier);
    }

    @Test
    void test_tier_gold_by_amount() {
        // 1 commande, 6000 DH cumulés
        CustomerTier tier = service.calculateTier(
                1,
                new BigDecimal("6000.00")
        );

        assertEquals(CustomerTier.GOLD, tier);
    }

    @Test
    void test_tier_platinum_by_amount() {
        // 1 commande, 16000 DH cumulés
        CustomerTier tier = service.calculateTier(
                1,
                new BigDecimal("16000.00")
        );

        assertEquals(CustomerTier.PLATINUM, tier);
    }
}