package com.epam.esm.dao;

import com.epam.esm.AppConfig;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@ActiveProfiles("test")
class GiftCertificateDaoTest {

    @Autowired
    private GiftCertificateDao giftCertificateDao;

    @Autowired
    private TagDao tagDao;
    private GiftCertificate giftCertificate1;
    private GiftCertificate giftCertificate2;

    @BeforeEach
    public void setUp() {
        giftCertificate1 = new GiftCertificate();
        giftCertificate1.setName("New Gift Certificate 1");
        giftCertificate1.setDescription("Gift Certificate 1 Description");
        giftCertificate1.setPrice(BigDecimal.valueOf(10.99));
        giftCertificate1.setDuration(5);

        giftCertificate2 = new GiftCertificate();
        giftCertificate2.setName("New Gift Certificate 2");
        giftCertificate2.setDescription("Gift Certificate 2 Description");
        giftCertificate2.setPrice(BigDecimal.valueOf(20.99));
        giftCertificate2.setDuration(10);
    }

    @AfterEach
    public void setDown() throws DbException {
        if (giftCertificate1.getId() != null) giftCertificateDao.delete(giftCertificate1.getId());
        if (giftCertificate2.getId() != null) giftCertificateDao.delete(giftCertificate2.getId());
    }

    @Test
    public void testCreateGiftCertificate() throws SQLException {
        giftCertificateDao.create(giftCertificate1);
        assertNotNull(giftCertificate1.getId());
        Optional<GiftCertificate> createdOptionalGiftCertificate = giftCertificateDao.getById(giftCertificate1.getId());
        assertTrue(createdOptionalGiftCertificate.isPresent());
        GiftCertificate createdGiftCertificate = createdOptionalGiftCertificate.get();

        assertEquals(giftCertificate1.getName(), createdGiftCertificate.getName());
        assertEquals(giftCertificate1.getDescription(), createdGiftCertificate.getDescription());
        assertEquals(giftCertificate1.getPrice(), createdGiftCertificate.getPrice());
        assertEquals(giftCertificate1.getDuration(), createdGiftCertificate.getDuration());
        assertNotNull(createdGiftCertificate.getLastUpdateDate());
        assertTrue(LocalDateTime.now().isAfter(createdGiftCertificate.getLastUpdateDate()));
        assertNotNull(createdGiftCertificate.getCreateDate());
        assertTrue(LocalDateTime.now().isAfter(createdGiftCertificate.getCreateDate()));
    }

    @Test
    public void testGetById() throws SQLException {
        giftCertificateDao.create(giftCertificate1);

        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(giftCertificate1.getId());

        assertTrue(optionalGiftCertificate.isPresent());
        GiftCertificate retrievedGiftCertificate = optionalGiftCertificate.get();
        assertEquals(giftCertificate1.getId(), retrievedGiftCertificate.getId());
        assertEquals(giftCertificate1.getName(), retrievedGiftCertificate.getName());
        assertEquals(giftCertificate1.getDescription(), retrievedGiftCertificate.getDescription());
        assertEquals(giftCertificate1.getPrice(), retrievedGiftCertificate.getPrice());
        assertEquals(giftCertificate1.getDuration(), retrievedGiftCertificate.getDuration());
        assertNotNull(retrievedGiftCertificate.getCreateDate());
        assertTrue(LocalDateTime.now().isAfter(retrievedGiftCertificate.getCreateDate()));
        assertNotNull(retrievedGiftCertificate.getLastUpdateDate());
        assertTrue(LocalDateTime.now().isAfter(retrievedGiftCertificate.getLastUpdateDate()));
    }

    @Test
    public void testUpdateGiftCertificateWithNewParams() throws SQLException {
        giftCertificateDao.create(giftCertificate1);

        giftCertificate1.setName("Gift Certificate 2");
        giftCertificate1.setDescription("Gift Certificate 2 Description");
        giftCertificate1.setPrice(BigDecimal.valueOf(20.99));
        giftCertificate1.setDuration(10);
        giftCertificateDao.update(giftCertificate1);

        // check that the new tag was added to the gift certificate and created in the database
        Optional<GiftCertificate>optionalGiftCertificate=giftCertificateDao.getById(giftCertificate1.getId());
        assertTrue(optionalGiftCertificate.isPresent());

        GiftCertificate retrievedGiftCertificate = optionalGiftCertificate.get();

        assertEquals(giftCertificate1.getId(), retrievedGiftCertificate.getId());
        assertEquals(giftCertificate1.getName(), retrievedGiftCertificate.getName());
        assertEquals(giftCertificate1.getDescription(), retrievedGiftCertificate.getDescription());
        assertEquals(giftCertificate1.getPrice(), retrievedGiftCertificate.getPrice());
        assertEquals(giftCertificate1.getDuration(), retrievedGiftCertificate.getDuration());
    }

        @Test
    public void testGetAll() throws SQLException {
        giftCertificateDao.create(giftCertificate1);
        giftCertificateDao.create(giftCertificate2);
        giftCertificate1.setTags(new ArrayList<>());
        giftCertificate2.setTags(new ArrayList<>());
        List<GiftCertificate> allCertificates = giftCertificateDao.getAll();
        for (GiftCertificate gs : allCertificates) {
            System.out.println(gs);
        }
        assertTrue(allCertificates.size() >= 2);
        assertTrue(allCertificates.contains(giftCertificate1));
        assertTrue(allCertificates.contains(giftCertificate2));
    }
}