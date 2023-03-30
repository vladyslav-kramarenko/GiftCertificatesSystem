package com.epam.esm.dao;

import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class GiftCertificateDaoTest {

    @Autowired
    private GiftCertificateDao giftCertificateDao;

    @Autowired
    private TagDao tagDao;
    private GiftCertificate giftCertificate1;
    private GiftCertificate giftCertificate2;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    public void setUp() {
        giftCertificate1 = new GiftCertificate();
        giftCertificate1.setName("Gift Certificate 1");
        giftCertificate1.setDescription("Gift Certificate 1 Description");
        giftCertificate1.setPrice(BigDecimal.valueOf(10.99));
        giftCertificate1.setDuration(5);

        giftCertificate2 = new GiftCertificate();
        giftCertificate2.setName("Gift Certificate 2");
        giftCertificate2.setDescription("Gift Certificate 2 Description");
        giftCertificate2.setPrice(BigDecimal.valueOf(20.99));
        giftCertificate2.setDuration(10);

        tag1 = new Tag();
        tag1.setName("Test Tag 1");

        tag2 = new Tag();
        tag2.setName("Test Tag 2");
    }

    @AfterEach
    public void setDown() {
        if (tag1.getId() != null) tagDao.delete(tag1.getId());
        if (tag2.getId() != null) tagDao.delete(tag2.getId());
        if (giftCertificate1.getId() != null) giftCertificateDao.delete(giftCertificate1.getId());
        if (giftCertificate2.getId() != null) giftCertificateDao.delete(giftCertificate2.getId());
    }

    @Test
    public void testCreateGiftCertificate() throws SQLException {
        tag1 = tagDao.create(tag1);

        // add tag to gift certificate
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        giftCertificate1.setTags(tags);

        // create gift certificate
        giftCertificateDao.create(giftCertificate1);
        assertNotNull(giftCertificate1.getId());
        GiftCertificate createdGiftCertificate = giftCertificateDao.getById(giftCertificate1.getId()).get();

//        createdGiftCertificate = giftCertificateDao.getById(createdId).get();
        assertEquals(giftCertificate1.getName(), createdGiftCertificate.getName());
        assertEquals(giftCertificate1.getDescription(), createdGiftCertificate.getDescription());
        assertEquals(giftCertificate1.getPrice(), createdGiftCertificate.getPrice());
        assertEquals(giftCertificate1.getDuration(), createdGiftCertificate.getDuration());
        assertEquals(giftCertificate1.getTags().size(), createdGiftCertificate.getTags().size());
        assertNotNull(createdGiftCertificate.getLastUpdateDate());
        assertTrue(LocalDateTime.now().isAfter(createdGiftCertificate.getLastUpdateDate()));
        assertNotNull(createdGiftCertificate.getCreateDate());
        assertTrue(LocalDateTime.now().isAfter(createdGiftCertificate.getCreateDate()));
    }

    @Test
    public void testGetById() throws SQLException {
        tagDao.create(tag1);

        // add tag to gift certificate
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        tags.add(tag2);
        giftCertificate1.setTags(tags);

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
    public void testCreateGiftCertificateWithTagThatExists() throws DbException {
        tagDao.create(tag1);

        List<Tag> tags = new ArrayList<>();
        Tag existingTag = new Tag();
        existingTag.setName(tag1.getName());
        tags.add(existingTag);
        giftCertificate1.setTags(tags);

        // create gift certificate
        giftCertificateDao.create(giftCertificate1);

        // check that the existing tag was added to the gift certificate and no new tag was created
        assertEquals(1, giftCertificate1.getTags().size());
        assertEquals(tag1.getId(), giftCertificate1.getTags().get(0).getId());
    }

    @Test
    public void testCreateGiftCertificateWithNewTag() throws DbException {
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        giftCertificate1.setTags(tags);

        // create gift certificate
        giftCertificateDao.create(giftCertificate1);

        // check that the new tag was added to the gift certificate and created in the database
        assertEquals(1, giftCertificate1.getTags().size());
        assertNotNull(giftCertificate1.getTags().get(0).getId());
        assertEquals(tag1.getName(), giftCertificate1.getTags().get(0).getName());
        assertNotNull(tagDao.getByName(tag1.getName()));
    }

    @Test
    public void testUpdateGiftCertificateWithNewTag() throws SQLException {
        giftCertificateDao.create(giftCertificate1);

        // add new tag to gift certificate and update
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        giftCertificate1.setTags(tags);
        giftCertificateDao.update(giftCertificate1);

        // check that the new tag was added to the gift certificate and created in the database
        giftCertificateDao.getById(giftCertificate1.getId()).get();
        assertEquals(1, giftCertificate1.getTags().size());
        assertNotNull(giftCertificate1.getTags().get(0).getId());
        assertEquals(tag1.getName(), giftCertificate1.getTags().get(0).getName());
        Optional<Tag> addedTag = tagDao.getByName(tag1.getName());
        assertTrue(addedTag.isPresent());
        assertEquals(tag1.getName(), addedTag.get().getName());

        tag1.setId(addedTag.get().getId());
    }

    @Test
//    @Transactional
    public void testGetAll() throws SQLException {
        tagDao.create(tag1);
        tagDao.create(tag2);

        // add tags to gift certificate 1
        List<Tag> tags1 = new ArrayList<>();
        tags1.add(tag1);
        tags1.add(tag2);
        giftCertificate1.setTags(tags1);

        // add tags to gift certificate 2
        List<Tag> tags2 = new ArrayList<>();
        tags2.add(tag1);
        giftCertificate2.setTags(tags2);

        // create gift certificates
        giftCertificateDao.create(giftCertificate1);
        giftCertificateDao.create(giftCertificate2);

        System.out.println(giftCertificateDao.getById(giftCertificate1.getId()));
        System.out.println(giftCertificateDao.getById(giftCertificate2.getId()));
        System.out.println(giftCertificateDao.getById(giftCertificate1.getId()));


        List<GiftCertificate> allCertificates = giftCertificateDao.getAll();
        for (GiftCertificate gs : allCertificates) {
            System.out.println(gs);
        }
        assertTrue(allCertificates.size() >= 2);
        boolean foundGiftCertificate1 = false;
        boolean foundGiftCertificate2 = false;
        for (GiftCertificate giftCertificate : allCertificates) {
            if (giftCertificate.getId().equals(giftCertificate1.getId())) {
                foundGiftCertificate1 = true;
            }
            if (giftCertificate.getId().equals(giftCertificate2.getId())) {
                foundGiftCertificate2 = true;
            }
        }

        assertTrue(foundGiftCertificate1);
        assertTrue(foundGiftCertificate2);
    }
}