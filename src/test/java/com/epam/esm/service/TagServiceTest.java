package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.Tag;
import com.epam.esm.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.esm.util.Constants.MAX_TAG_NAME_LENGTH;
import static com.epam.esm.util.testUtils.generateStringBySize;
import static com.epam.esm.util.testUtils.generateTag;
import static org.junit.jupiter.api.Assertions.*;

public class TagServiceTest {
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;

    @Mock
    private TagDao tagDao;
    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTagById_ValidId_ReturnsTag() throws ServiceException, DbException {
        Tag tag = new Tag();
        tag.setId(VALID_ID);
        Mockito.when(tagDao.getById(VALID_ID)).thenReturn(Optional.of(tag));

        Optional<Tag> result = tagService.getTagById(VALID_ID);

        assertTrue(result.isPresent());
        assertEquals(tag, result.get());
        Mockito.verify(tagDao).getById(VALID_ID);
    }

    @Test
    public void testGetTagById_InvalidId_ThrowsIllegalArgumentException() throws DbException {
        Mockito.when(tagDao.getById(INVALID_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> tagService.getTagById(INVALID_ID));
    }

    @Test
    public void testGetTagById_NullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.getTagById(null));
    }

    @Test
    public void testCreateTag_ValidTag_ReturnsCreatedTag() throws ServiceException, DbException {
        Tag tag = new Tag();
        tag.setName("Test Tag");
        Mockito.when(tagDao.create(tag)).thenReturn(tag);

        Tag result = tagService.createTag(tag);

        assertEquals(tag, result);
        Mockito.verify(tagDao).create(tag);
    }

    @Test
    public void testCreateTag_NullTag_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(null));
    }

    @Test
    public void testDeleteTag_ValidId_ReturnsTrue() throws ServiceException, DbException {
        Mockito.when(tagDao.delete(VALID_ID)).thenReturn(true);

        boolean result = tagService.deleteTag(VALID_ID);

        assertTrue(result);
        Mockito.verify(tagDao).delete(VALID_ID);
    }

    @Test
    public void testDeleteTag_InvalidId_ReturnsFalse() {
        assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(INVALID_ID));
    }

    @Test
    public void testDeleteTag_NullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(null));
    }

    @Test
    public void testGetTags_ValidSortParams_ReturnsSortedTags() throws ServiceException, DbException {
        List<Tag> tags = new ArrayList<>();

        tags.add(generateTag(1L));
        tags.add(generateTag(2L));

        Sort sort = Sort.by("name").ascending();
        Mockito.when(tagDao.getAll(sort)).thenReturn(tags);

        List<Tag> result = tagService.getTags(new String[]{"name", "asc"});
        assertEquals(tags, result);
        Mockito.verify(tagDao).getAll(sort);

        sort = Sort.by("id").descending();
        tags = tags.stream().sorted((a, b) -> (int) (b.getId() - a.getId())).collect(Collectors.toList());
        Mockito.when(tagDao.getAll(sort)).thenReturn(tags);

        result = tagService.getTags(new String[]{"id", "desc"});
        assertEquals(tags, result);
        Mockito.verify(tagDao).getAll(sort);
    }

    @Test
    public void testGetTags_NullSortParams_ReturnsUnsortedTags() throws ServiceException, DbException {
        List<Tag> tags = new ArrayList<>();

        tags.add(generateTag(1L));
        tags.add(generateTag(2L));

        Mockito.when(tagDao.getAll(null)).thenReturn(tags);

        List<Tag> result = tagService.getTags(null);
        assertEquals(tags, result);
        Mockito.verify(tagDao).getAll(null);
    }

    @Test
    public void testGetTags_InvalidSortParams_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.getTags(new String[]{"invalidField", "asc"}));
    }

    @Test
    public void testCreateTag_InvalidTag_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(null));

        Tag invalidTag = new Tag();
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(invalidTag));

        invalidTag.setName("");
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(invalidTag));

        invalidTag.setName(generateStringBySize(MAX_TAG_NAME_LENGTH + 1));
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(invalidTag));
    }
}
