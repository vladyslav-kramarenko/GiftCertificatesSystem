package com.epam.esm.core.service.util;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.util.Utilities;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
class UtilitiesTest {

    @Test
    void concatSort_validSort_returnsConcatenatedSortString() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name").and(Sort.by(Sort.Direction.DESC, "price"));
        String defaultSort = "id asc";
        String concatenatedSort = Utilities.concatSort(sort, defaultSort);

        assertEquals("name ASC, price DESC", concatenatedSort);
    }

    @Test
    void concatSort_emptySort_returnsDefaultSortString() {
        Sort sort = Sort.unsorted();
        String defaultSort = "id asc";
        String concatenatedSort = Utilities.concatSort(sort, defaultSort);

        assertEquals(defaultSort, concatenatedSort);
    }

    @Test
    void concatSort_nullSort_returnsDefaultSortString() {
        Sort sort = null;
        String defaultSort = "id asc";
        String concatenatedSort = Utilities.concatSort(sort, defaultSort);

        assertEquals(defaultSort, concatenatedSort);
    }

    @Test
    void calculateOrderSum_validGiftCertificatesWithQuantity_returnsOrderSum() {
        Map<GiftCertificate, Integer> giftCertificatesWithQuantity = new HashMap<>();
        GiftCertificate gc1 = new GiftCertificate();
        gc1.setId(1L);
        gc1.setName("GC1");
        gc1.setDescription("Description 1");
        gc1.setPrice(BigDecimal.valueOf(10.0));
        gc1.setDuration(30);
        giftCertificatesWithQuantity.put(gc1, 2);

        GiftCertificate gc2 = new GiftCertificate();
        gc2.setId(2L);
        gc2.setName("GC2");
        gc2.setDescription("Description 2");
        gc2.setPrice(BigDecimal.valueOf(20.0));
        gc2.setDuration(30);
        giftCertificatesWithQuantity.put(gc2, 3);

        BigDecimal orderSum = Utilities.calculateOrderSum(giftCertificatesWithQuantity);

        assertEquals(BigDecimal.valueOf(80.0), orderSum);
    }

    @Test
    void calculateOrderSum_emptyGiftCertificatesWithQuantity_returnsZeroOrderSum() {
        Map<GiftCertificate, Integer> giftCertificatesWithQuantity = new HashMap<>();

        BigDecimal orderSum = Utilities.calculateOrderSum(giftCertificatesWithQuantity);

        assertEquals(BigDecimal.ZERO, orderSum);
    }
}