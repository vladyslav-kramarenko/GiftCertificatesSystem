package com.epam.esm.util;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public class Utilities {
    public static boolean validateId(Long id) {
        if (id != null && id >= 0) {
            return true;
        }
        throw new IllegalArgumentException("Wrong ID: " + id);
    }

    public static Sort createSort(String[] sortParams,String[] allowedSortFields, String[] allowedSortDirections) throws IllegalArgumentException {
        Sort sort = null;
        for (int i = 0; i < sortParams.length; i += 2) {
            String field = sortParams[i];
            if (Arrays.stream(allowedSortFields).noneMatch(x -> x.equalsIgnoreCase(field))) {
                throw new IllegalArgumentException("sort param " + field + " is not allowed");
            }
            String direction = sortParams.length > i + 1 ? sortParams[i + 1] : "asc";
            if (Arrays.stream(allowedSortDirections).noneMatch(x -> x.equalsIgnoreCase(direction))) {
                throw new IllegalArgumentException("sort direction " + field + " is not allowed");
            }
            if (sort == null) {
                sort = Sort.by(Sort.Direction.fromString(direction), field);
            } else {
                sort = sort.and(Sort.by(Sort.Direction.fromString(direction), field));
            }
        }
        return sort;
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
