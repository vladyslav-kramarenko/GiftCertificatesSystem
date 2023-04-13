package com.epam.esm.dao;

import com.epam.esm.AppConfig;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.epam.esm.util.SortUtilities.addSortFieldAndDirectionToSort;
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
    public void testAddTagToCertificate() throws DbException {
        Tag newTag = tagDao.create(new Tag(null, "Some New Tag"));

        giftCertificateDao.create(giftCertificate1);
        giftCertificateDao.addTagToCertificate(giftCertificate1, newTag);

        GiftCertificate certificateWithTags = giftCertificateDao.getById(giftCertificate1.getId()).orElse(null);
        assertNotNull(certificateWithTags);
        assertTrue(certificateWithTags.getTags().contains(newTag));

        tagDao.delete(newTag.id());
    }

    @Test
    public void testDeleteAllTagsForCertificateById() throws DbException {
        // Create two new tags and add them to the database
        Tag newTag2 =tagDao.create(new Tag(null,"New Tag 1"));
        Tag newTag1 =tagDao.create(new Tag(null,"New Tag 2"));

        // Create a gift certificate and add the tags to it
        giftCertificateDao.create(giftCertificate1);
        giftCertificateDao.addTagToCertificate(giftCertificate1, newTag1);
        giftCertificateDao.addTagToCertificate(giftCertificate1, newTag2);

        // Delete all tags for the gift certificate
        giftCertificateDao.deleteAllTagsForCertificateById(giftCertificate1.getId());

        // Verify that the tags have been removed
        List<Tag> tagsAfterDeletion = giftCertificateDao.getById(giftCertificate1.getId()).get().getTags();
        assertTrue(tagsAfterDeletion.isEmpty());

        // Clean up the tags from the database
        tagDao.delete(newTag1.id());
        tagDao.delete(newTag2.id());
    }

    @Test
    public void testAddExistingTagToCertificate() throws DbException {
        // Create new tag and add it to the database
        Tag CreatedTag = tagDao.create(new Tag(null, "Some New Tag"));

        // Add the tag to the certificate
        giftCertificateDao.create(giftCertificate1);
        giftCertificateDao.addTagToCertificate(giftCertificate1, CreatedTag);

        // Try to add the same tag again to the certificate and expect an exception
        assertThrows(IllegalArgumentException.class, () -> giftCertificateDao.addTagToCertificate(giftCertificate1, CreatedTag));

        // Clean up the tag from the database
        tagDao.delete(CreatedTag.id());
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
        Optional<GiftCertificate> optionalGiftCertificate = giftCertificateDao.getById(giftCertificate1.getId());
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

    @Test
    public void testGetCertificatesByTagId() throws DbException {
        // Create a new tag and add it to the database
        Tag newTag = tagDao.create(new Tag(null,"Test Tag"));

        // Create two gift certificates and add the new tag to them
        giftCertificateDao.create(giftCertificate1);
        giftCertificateDao.create(giftCertificate2);
        giftCertificateDao.addTagToCertificate(giftCertificate1, newTag);
        giftCertificateDao.addTagToCertificate(giftCertificate2, newTag);

        // Get gift certificates by the new tag's ID
        List<GiftCertificate> giftCertificatesByTag = giftCertificateDao.getCertificatesByTagId(newTag.id());

        // Verify that the retrieved gift certificates match the expected ones
        assertEquals(2, giftCertificatesByTag.size());
        assertTrue(giftCertificatesByTag.stream().anyMatch(cert -> cert.getId().equals(giftCertificate1.getId())));
        assertTrue(giftCertificatesByTag.stream().anyMatch(cert -> cert.getId().equals(giftCertificate2.getId())));

        // Clean up the tag from the database
        tagDao.delete(newTag.id());
    }
}