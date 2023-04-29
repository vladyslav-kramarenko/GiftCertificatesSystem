package com.epam.esm.core.service.util;
import com.epam.esm.core.util.SortUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SortUtilitiesTest {

    private String[] allowedSortFields;
    private String[] allowedSortDirections;

    @BeforeEach
    void setUp() {
        allowedSortFields = new String[]{"name", "description", "price", "duration"};
        allowedSortDirections = new String[]{"asc", "desc"};
    }

    @Test
    void createSort_validParams_returnsSort() {
        String[] sortParams = {"name", "asc", "price", "desc"};
        Optional<Sort> sort = SortUtilities.createSort(sortParams, allowedSortFields, allowedSortDirections);

        assertTrue(sort.isPresent());
        assertEquals("name: ASC,price: DESC", sort.get().toString());
    }

    @Test
    void createSort_nullParams_returnsEmpty() {
        String[] sortParams = null;
        Optional<Sort> sort = SortUtilities.createSort(sortParams, allowedSortFields, allowedSortDirections);

        assertFalse(sort.isPresent());
    }

    @Test
    void createSort_invalidSortField_throwsIllegalArgumentException() {
        String[] sortParams = {"invalid", "asc"};
        assertThrows(IllegalArgumentException.class, () -> SortUtilities.createSort(sortParams, allowedSortFields, allowedSortDirections));
    }

    @Test
    void createSort_invalidSortDirection_throwsIllegalArgumentException() {
        String[] sortParams = {"name", "invalid"};
        assertThrows(IllegalArgumentException.class, () -> SortUtilities.createSort(sortParams, allowedSortFields, allowedSortDirections));
    }

    @Test
    void createSort_emptyAllowedSortFields_throwsIllegalArgumentException() {
        String[] emptyAllowedSortFields = new String[]{};
        String[] sortParams = {"name", "asc"};
        assertThrows(IllegalArgumentException.class, () -> SortUtilities.createSort(sortParams, emptyAllowedSortFields, allowedSortDirections));
    }

    @Test
    void createSort_emptyAllowedSortDirections_throwsIllegalArgumentException() {
        String[] emptyAllowedSortDirections = new String[]{};
        String[] sortParams = {"name", "asc"};
        assertThrows(IllegalArgumentException.class, () -> SortUtilities.createSort(sortParams, allowedSortFields, emptyAllowedSortDirections));
    }
}