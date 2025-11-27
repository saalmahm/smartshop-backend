package ma.smartshop.smartshop.service;

import ma.smartshop.smartshop.enums.CustomerTier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoyaltyService {

    public CustomerTier calculateTier(int totalOrders, BigDecimal totalSpent) {
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        if (totalOrders >= 20 || totalSpent.compareTo(BigDecimal.valueOf(15000)) >= 0) {
            return CustomerTier.PLATINUM;
        }

        if (totalOrders >= 10 || totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            return CustomerTier.GOLD;
        }

        if (totalOrders >= 3 || totalSpent.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return CustomerTier.SILVER;
        }

        return CustomerTier.BASIC;
    }
}