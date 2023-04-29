package com.epam.esm.core.service.util;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.util.GiftCertificateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class GiftCertificateUtilsTest {
    private GiftCertificate giftCertificateToUpdate;

    @BeforeEach
    public void setUp() {
        giftCertificateToUpdate = new GiftCertificate();
        giftCertificateToUpdate.setName("Old Name");
        giftCertificateToUpdate.setDescription("Old Description");
        giftCertificateToUpdate.setDuration(5);
        giftCertificateToUpdate.setPrice(BigDecimal.valueOf(50));
    }

    @Test
    public void updateCertificate_allFieldsUpdated() {
        GiftCertificate giftCertificateWithNewData = createGiftCertificate("New Name", "New Description", 10, BigDecimal.valueOf(100));

        GiftCertificateUtils.updateCertificate(giftCertificateToUpdate, giftCertificateWithNewData);

        assertEquals("New Name", giftCertificateToUpdate.getName());
        assertEquals("New Description", giftCertificateToUpdate.getDescription());
        assertEquals(10, giftCertificateToUpdate.getDuration());
        assertEquals(BigDecimal.valueOf(100), giftCertificateToUpdate.getPrice());
    }

    @Test
    public void updateCertificate_someFieldsUpdated() {
        GiftCertificate giftCertificateWithNewData = createGiftCertificate("New Name", null, 10, null);

        GiftCertificateUtils.updateCertificate(giftCertificateToUpdate, giftCertificateWithNewData);

        assertEquals("New Name", giftCertificateToUpdate.getName());
        assertEquals("Old Description", giftCertificateToUpdate.getDescription());
        assertEquals(10, giftCertificateToUpdate.getDuration());
        assertEquals(BigDecimal.valueOf(50), giftCertificateToUpdate.getPrice());
    }

    @Test
    public void updateCertificate_noFieldsUpdated() {
        GiftCertificate giftCertificateWithNewData = createGiftCertificate(null, null, null, null);

        GiftCertificateUtils.updateCertificate(giftCertificateToUpdate, giftCertificateWithNewData);

        assertEquals("Old Name", giftCertificateToUpdate.getName());
        assertEquals("Old Description", giftCertificateToUpdate.getDescription());
        assertEquals(5, giftCertificateToUpdate.getDuration());
        assertEquals(BigDecimal.valueOf(50), giftCertificateToUpdate.getPrice());
    }

    private GiftCertificate createGiftCertificate(String name, String description, Integer duration, BigDecimal price) {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setName(name);
        giftCertificate.setDescription(description);
        giftCertificate.setDuration(duration);
        giftCertificate.setPrice(price);
        return giftCertificate;
    }
}