package com.epam.esm.service;

import com.epam.esm.AppConfig;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.filter.GiftCertificateFilter;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.service.impl.GiftCertificateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ContextConfiguration(classes = {AppConfig.class})
@ActiveProfiles("test")
public class GiftCertificateServiceTest {
    private GiftCertificateDao giftCertificateDao;
    private TagDao tagDao;
    private GiftCertificateServiceImpl giftCertificateService;

    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static final int PAGE = 0;
    private static final int SIZE = 10;
    private static final String[] SORT_PARAMS = {"name", "asc"};

    @BeforeEach
    public void setUp() {
        giftCertificateDao = mock(GiftCertificateDao.class);
        tagDao = mock(TagDao.class);
        giftCertificateService = new GiftCertificateServiceImpl(giftCertificateDao, tagDao);
    }

    @Test
    public void testGetGiftCertificates_WithFilter_ReturnsFilteredAndSortedGiftCertificates() throws DbException, ServiceException {
        List<GiftCertificate> giftCertificates = new ArrayList<>();

        GiftCertificate giftCertificate1 = generateGiftCertificateWithIdAndName(1L);
        GiftCertificate giftCertificate2 = generateGiftCertificateWithIdAndName(2L);
        GiftCertificate giftCertificate3 = generateGiftCertificateWithIdAndName(3L);

        Tag tag1 = generateTagWithId(1L);
        Tag tag2 = generateTagWithId(2L);

        List<Tag> tags1 = new ArrayList<>();
        List<Tag> tags2 = new ArrayList<>();

        tags1.add(tag1);
        tags2.add(tag2);

        giftCertificate1.setTags(tags1);
        giftCertificate2.setTags(tags1);

        giftCertificate3.setTags(tags2);

        giftCertificates.add(giftCertificate1);
        giftCertificates.add(giftCertificate2);
        giftCertificates.add(giftCertificate3);

        GiftCertificateFilter filter = GiftCertificateFilter.builder()
                .withTagName(tag1.getName())
                .build();

        Sort sort = Sort.by("name").ascending();
        when(giftCertificateDao.getAllWithSearchQuery(filter.getSearchQuery(), sort)).thenReturn(giftCertificates);

        List<GiftCertificate> result = giftCertificateService.getGiftCertificates(filter, PAGE, SIZE, SORT_PARAMS);

        assertEquals(2, result.size());
        assertEquals(giftCertificate1, result.get(0));
        assertEquals(giftCertificate2, result.get(1));
        verify(giftCertificateDao).getAllWithSearchQuery(filter.getSearchQuery(), sort);
    }

    @Test
    public void testGetGiftCertificates_WithoutFilter_ReturnsAllGiftCertificates() throws DbException, ServiceException {
        List<GiftCertificate> giftCertificates = new ArrayList<>();

        GiftCertificate giftCertificate1 = generateGiftCertificateWithIdAndName(1L);
        GiftCertificate giftCertificate2 = generateGiftCertificateWithIdAndName(2L);

        giftCertificates.add(giftCertificate1);
        giftCertificates.add(giftCertificate2);

        GiftCertificateFilter filter = GiftCertificateFilter.builder().build();

        Sort sort = Sort.by("name").ascending();
        when(giftCertificateDao.getAllWithSearchQuery(filter.getSearchQuery(), sort)).thenReturn(giftCertificates);

        List<GiftCertificate> result = giftCertificateService.getGiftCertificates(filter, PAGE, SIZE, SORT_PARAMS);

        assertEquals(2, result.size());
        assertEquals(giftCertificate1, result.get(0));
        assertEquals(giftCertificate2, result.get(1));
        verify(giftCertificateDao).getAllWithSearchQuery(filter.getSearchQuery(), sort);
    }

    @Test
    public void testGetGiftCertificateById() throws Exception {
        GiftCertificate expectedGiftCertificate = new GiftCertificate();
        expectedGiftCertificate.setId(VALID_ID);
        when(giftCertificateDao.getById(VALID_ID)).thenReturn(Optional.of(expectedGiftCertificate));

        Optional<GiftCertificate> actualGiftCertificate = giftCertificateService.getGiftCertificateById(VALID_ID);
        assertTrue(actualGiftCertificate.isPresent());
        assertEquals(expectedGiftCertificate, actualGiftCertificate.get());
        verify(giftCertificateDao).getById(VALID_ID);
    }

    @Test
    public void testGetGiftCertificateByInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> giftCertificateService.getGiftCertificateById(INVALID_ID));
    }

    @Test
    public void testCreateGiftCertificate() throws Exception {
        GiftCertificate giftCertificate = generateGiftCertificateWithoutId();
        giftCertificate.setId(VALID_ID);

        when(giftCertificateDao.create(giftCertificate)).thenReturn(giftCertificate);
        when(giftCertificateDao.getById(VALID_ID)).thenReturn(Optional.of(giftCertificate));
        when(tagDao.create(giftCertificate.getTags().get(0))).thenReturn(giftCertificate.getTags().get(0));
        when(tagDao.create(giftCertificate.getTags().get(1))).thenReturn(giftCertificate.getTags().get(1));

        GiftCertificate createdGiftCertificate = giftCertificateService.createGiftCertificate(giftCertificate);

        assertEquals(giftCertificate, createdGiftCertificate);
        verify(giftCertificateDao).create(giftCertificate);
    }

    @Test
    public void testUpdateGiftCertificate() throws Exception {
        GiftCertificate existingGiftCertificate = generateGiftCertificateWithoutId();
        existingGiftCertificate.setId(VALID_ID);

        GiftCertificate updatedGiftCertificate = new GiftCertificate();
        updatedGiftCertificate.setName("Updated Gift Certificate");
        updatedGiftCertificate.setDescription("Updated Description");
        updatedGiftCertificate.setPrice(BigDecimal.valueOf(50.00));
        updatedGiftCertificate.setDuration((110));
        List<Tag> updatedTags = new ArrayList<>();
        updatedTags.add(new Tag(3L, "Updated Tag 1"));
        updatedTags.add(new Tag(4L, "Updated Tag 2"));
        updatedGiftCertificate.setTags(updatedTags);
        updatedGiftCertificate.setId(VALID_ID);

        when(giftCertificateDao.getById(VALID_ID)).thenReturn(Optional.of(existingGiftCertificate));
        when(giftCertificateDao.update(existingGiftCertificate)).thenReturn(updatedGiftCertificate);

        Optional<GiftCertificate> actualGiftCertificate = giftCertificateService.updateGiftCertificate(VALID_ID, updatedGiftCertificate);

        assertTrue(actualGiftCertificate.isPresent());
        assertEquals(updatedGiftCertificate, actualGiftCertificate.get());
        verify(giftCertificateDao).update(existingGiftCertificate);
    }

    @Test
    public void testUpdateGiftCertificateWithInvalidId() {
        GiftCertificate giftCertificate = generateGiftCertificateWithoutId();
        assertThrows(IllegalArgumentException.class, () -> giftCertificateService.updateGiftCertificate(INVALID_ID, giftCertificate));
    }

    @Test
    public void testDeleteGiftCertificate() throws Exception {
        when(giftCertificateDao.delete(VALID_ID)).thenReturn(true);

        boolean result = giftCertificateService.deleteGiftCertificate(VALID_ID);

        assertTrue(result);
        verify(giftCertificateDao).delete(VALID_ID);
    }
}
