package com.epam.esm.core.util;

import org.springframework.data.domain.Sort;


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
}
