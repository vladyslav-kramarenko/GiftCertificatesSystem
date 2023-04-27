package com.epam.esm.core.util;

import com.epam.esm.core.entity.GiftCertificate;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Map;


/**
 * A utility class with helper methods.
 */
public class Utilities {
    public static String concatSort(Sort sort, String defaultSort) {
        if (sort == null || sort.isEmpty()) return defaultSort;
        StringBuilder sb = new StringBuilder();
        for (Sort.Order order : sort) {
            sb.append(order.getProperty()).append(" ")
                    .append(order.getDirection().name().toUpperCase()).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public static BigDecimal calculateOrderSum(Map<GiftCertificate, Integer> giftCertificatesWithQuantity) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<GiftCertificate, Integer> entry : giftCertificatesWithQuantity.entrySet()) {
            GiftCertificate giftCertificate = entry.getKey();
            int count = entry.getValue();
            sum = sum.add(giftCertificate.getPrice().multiply(new BigDecimal(count)));
        }
        return sum;
    }
}
