package com.epam.esm.util;

import com.epam.esm.exception.DbException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * A utility class with helper methods.
 */
public class Utilities {
    /**
     * Validates if the given ID is not null and greater than or equal to zero.
     *
     * @param id the ID to validate
     * @throws IllegalArgumentException if the ID is not valid
     */
    public static void validateId(Long id) {
        if (id == null || id < 0)
            throw new IllegalArgumentException("Wrong ID: " + id);
    }

    /**
     * Returns an ORDER BY clause for the given SQL query based on the provided Sort object.
     *
     * @param sql  the SQL query to modify
     * @param sort the Sort object containing sort parameters
     * @return the modified SQL query with the ORDER BY clause
     */
    public static String getOrderByClause(String sql, Sort sort) {
        if (sort == null || sort.isEmpty()) return sql;
        return sql + " ORDER BY " + concatSort(sort, "");
    }

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

    /**
     * Returns the generated key from the given KeyHolder object.
     *
     * @param keyHolder the KeyHolder object containing the generated key
     * @return the generated key as a long value
     * @throws DbException if the generated key is not found or cannot be converted to a long value
     */
    public static long getKey(KeyHolder keyHolder) throws DbException {
        try {
            Object keyObject = keyHolder.getKeys().getOrDefault("GENERATED_KEY", null);
            if (keyObject != null) {
                BigInteger key = (BigInteger) keyObject;
                return key.longValue();
            } else {
                keyObject = keyHolder.getKeys().get("id");
                if (keyObject == null) keyObject = keyHolder.getKeys().get("ID");
                if (keyObject == null) throw new DbException("Generated key not found");
                return (long) keyObject;
            }
        } catch (Exception e) {
            throw new DbException("Generated key cannot be converted to a long value: " + Arrays.toString(e.getStackTrace()));
        }
    }
}
