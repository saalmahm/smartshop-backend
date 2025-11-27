package ma.smartshop.smartshop.service;

import ma.smartshop.smartshop.entity.Client;
import ma.smartshop.smartshop.enums.CustomerTier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderCalculationService {

    private static final BigDecimal TVA_RATE = new BigDecimal("0.20");
    private static final BigDecimal SILVER_DISCOUNT_RATE = new BigDecimal("0.05");
    private static final BigDecimal GOLD_DISCOUNT_RATE = new BigDecimal("0.10");
    private static final BigDecimal PLATINUM_DISCOUNT_RATE = new BigDecimal("0.15");
    private static final BigDecimal PROMO_DISCOUNT_RATE = new BigDecimal("0.05");

    public OrderAmounts calculate(BigDecimal subTotalHt, Client client, String promoCode) {
        BigDecimal roundedSubTotal = round(subTotalHt);

        CustomerTier tier = client != null && client.getTier() != null
                ? client.getTier()
                : CustomerTier.BASIC;

        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(roundedSubTotal, tier);
        BigDecimal promoDiscount = calculatePromoDiscount(roundedSubTotal, promoCode);

        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount);
        if (totalDiscount.compareTo(roundedSubTotal) > 0) {
            totalDiscount = roundedSubTotal;
        }

        BigDecimal totalHtAfterDiscount = roundedSubTotal.subtract(totalDiscount);
        BigDecimal tvaAmount = totalHtAfterDiscount.multiply(TVA_RATE);
        BigDecimal totalTtc = totalHtAfterDiscount.add(tvaAmount);

        return new OrderAmounts(
                round(roundedSubTotal),
                round(totalDiscount),
                round(totalHtAfterDiscount),
                round(tvaAmount),
                round(totalTtc)
        );
    }

    private BigDecimal calculateLoyaltyDiscount(BigDecimal amount, CustomerTier tier) {
        if (tier == CustomerTier.SILVER) {
            if (amount.compareTo(new BigDecimal("500")) >= 0) {
                return amount.multiply(SILVER_DISCOUNT_RATE);
            }
            return BigDecimal.ZERO;
        }

        if (tier == CustomerTier.GOLD) {
            if (amount.compareTo(new BigDecimal("800")) >= 0) {
                return amount.multiply(GOLD_DISCOUNT_RATE);
            }
            return BigDecimal.ZERO;
        }

        if (tier == CustomerTier.PLATINUM) {
            if (amount.compareTo(new BigDecimal("1200")) >= 0) {
                return amount.multiply(PLATINUM_DISCOUNT_RATE);
            }
            return BigDecimal.ZERO;
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculatePromoDiscount(BigDecimal amount, String promoCode) {
        if (promoCode != null && promoCode.matches("PROMO-[A-Z0-9]{4}")) {
            return amount.multiply(PROMO_DISCOUNT_RATE);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public record OrderAmounts(
            BigDecimal subTotalHt,
            BigDecimal discountAmount,
            BigDecimal totalHtAfterDiscount,
            BigDecimal tvaAmount,
            BigDecimal totalTtc
    ) {
    }
}