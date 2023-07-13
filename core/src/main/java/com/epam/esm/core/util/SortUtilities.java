package com.epam.esm.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Optional;

public class SortUtilities {
    private static final Logger logger = LoggerFactory.getLogger(SortUtilities.class);

    public static Optional<Sort> createSort(String[] sortParams, String[] allowedSortFields, String[] allowedSortDirections) throws IllegalArgumentException {
        Sort sort = null;

        if (sortParams == null) {
            logger.info("sortParams is null");
            return Optional.empty();
        }
        validateAllowedSortDirections(allowedSortDirections);
        validateAllowedSortFields(allowedSortFields);

        String defaultSortDirection = "asc";

        for (int i = 0; i < sortParams.length; i += 2) {
            String field = sortParams[i];
            validateSortField(field, allowedSortFields);

            String direction = sortParams.length > i + 1 ? sortParams[i + 1] : defaultSortDirection;
            validateSortDirection(direction, allowedSortDirections);

            sort = addSortFieldAndDirectionToSort(sort, field, direction);
        }
        return sort == null ? Optional.empty() : Optional.of(sort);
    }

    public static Sort addSortFieldAndDirectionToSort(Sort sort, String field, String direction) {
        if (sort == null) {
            return Sort.by(Sort.Direction.fromString(direction), field);
        }
        return sort.and(Sort.by(Sort.Direction.fromString(direction), field));
    }

    public static void validateSortField(String field, String[] allowedSortFields) {
        if (Arrays.stream(allowedSortFields).noneMatch(x -> x.equalsIgnoreCase(field))) {
            throw new IllegalArgumentException("sort param " + field + " is not allowed");
        }
    }

    public static void validateSortDirection(String direction, String[] allowedSortDirections) {
        if (Arrays.stream(allowedSortDirections).noneMatch(x -> x.equalsIgnoreCase(direction))) {
            throw new IllegalArgumentException("sort direction " + direction + " is not allowed");
        }
    }

    public static void validateAllowedSortDirections(String[] allowedSortDirections) throws IllegalArgumentException {
        if (allowedSortDirections == null || allowedSortDirections.length == 0)
            throw new IllegalArgumentException("Allowed sort directions can not be empty");
    }

    public static void validateAllowedSortFields(String[] allowedSortFields) throws IllegalArgumentException {
        if (allowedSortFields == null || allowedSortFields.length == 0)
            throw new IllegalArgumentException("Allowed sort fields can not be empty");
    }
}
