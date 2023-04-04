package com.epam.esm.util;

import org.springframework.data.domain.Sort;

public class Utilities {
    public static boolean validateId(Long id) {
        if (id != null && id >= 0) {
            return true;
        }
        throw new IllegalArgumentException("Wrong ID: " + id);
    }

    public static String getOrderByClause(String sql, Sort sort) {
        if (sort == null || sort.isEmpty()) return sql;
        StringBuilder sb = new StringBuilder(sql);
        sb.append(" ");
        sb.append("ORDER BY ");

        for (Sort.Order order : sort) {
            sb.append(order.getProperty()).append(" ")
                    .append(order.getDirection().name().toUpperCase()).append(", ");
        }

        // Remove the trailing comma and space
        sb.delete(sb.length() - 2, sb.length());
        System.out.println(sb);
        return sb.toString();
    }
}
