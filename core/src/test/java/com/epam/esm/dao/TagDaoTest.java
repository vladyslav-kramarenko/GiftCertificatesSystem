package com.epam.esm.dao;

import com.epam.esm.AppConfig;
import com.epam.esm.exception.DbException;
import com.epam.esm.model.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@ActiveProfiles("test")
public class TagDaoTest {
    @Autowired
    private GiftCertificateDao giftCertificateDao;
    @Autowired
    private TagDao tagDao;

    @Test
    void testCreateAndGetById() throws DbException {
        // given
        Tag tag = new Tag(null, "Test tag");

        // when
        Tag createdTag = tagDao.create(tag);
        Optional<Tag> tagOptional = tagDao.getById(createdTag.id());

        // then
        assertTrue(tagOptional.isPresent());
        assertEquals(createdTag, tagOptional.get());

        tagDao.delete(tagOptional.get().id());
    }

    @Test
    void testGetByName() throws DbException {
        // given
        Tag tag = new Tag(null, "Test tag");
        tag = tagDao.create(tag);

        // when
        Optional<Tag> tagOptional = tagDao.getByName("Test tag");

        // then
        assertTrue(tagOptional.isPresent());
        assertEquals(tag, tagOptional.get());
        assertEquals(tag.id(), tagOptional.get().id());

        tagDao.delete(tagOptional.get().id());
    }

    @Test
    void testGetAll() throws DbException {
        // given
        List<Tag> tags = tagDao.getAll();
        int oldSize = tags.size();

        Tag tag1 = new Tag(null, "Tag 1 new");
        Tag tag2 = new Tag(null, "Tag 2 new");
        tag1 = tagDao.create(tag1);
        tag2 = tagDao.create(tag2);

        // when
        tags = tagDao.getAll();

        // then
        assertEquals(oldSize + 2, tags.size());
        assertTrue(tags.contains(tag1));
        assertTrue(tags.contains(tag2));

        tagDao.delete(tag1.id());
        tagDao.delete(tag2.id());
    }

    @Test
    void testDelete() throws DbException {
        // given
        Tag tag = new Tag(null, "Test tag");
        tagDao.create(tag);
        Optional<Tag> tagOptional = tagDao.getByName("Test tag");
        assertTrue(tagOptional.isPresent());

        // when
        tagDao.delete(tagOptional.get().id());
        tagOptional = tagDao.getByName("Test tag");

        // then
        assertFalse(tagOptional.isPresent());
    }

    @Test
    public void testCreateDuplicateTag() throws DbException {
        Tag tag = new Tag(null, "Tag");
        // First create a tag
        tagDao.create(tag);
        // Attempt to create the same tag again
        assertThrows(IllegalArgumentException.class, () -> tagDao.create(tag));
        tagDao.delete(tag.id());
    }

    @Test
    public void testUpdateTag(){
        assertThrows(DbException.class, () -> tagDao.update(new Tag(null, "")));
    }
}
