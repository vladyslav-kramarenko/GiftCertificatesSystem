package com.epam.esm.core.service;

import com.epam.esm.core.CoreTestApplication;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = CoreTestApplication.class)
class TagServiceImplTest {
    @Mock
    private TagRepository tagRepository;
    @Mock
    private GiftCertificateRepository giftCertificateRepository;
    @InjectMocks
    private TagServiceImpl tagService;
    private Tag testTag;
    private Tag testTag2;
    private GiftCertificate testGiftCertificate;

    @BeforeEach
    public void setUp() {
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("Test Tag");

        testTag2 = new Tag();
        testTag2.setId(2L);
        testTag2.setName("Test Tag 2");


        testGiftCertificate = new GiftCertificate();
        testGiftCertificate.setId(1L);
        testGiftCertificate.setName("Test Gift Certificate");
        testGiftCertificate.setDescription("Test Description");
        testGiftCertificate.setPrice(BigDecimal.valueOf(100));
        testGiftCertificate.setDuration(30);
        testGiftCertificate.setTags(Collections.singletonList(testTag));
    }

    @Test
    public void testGetTagById() throws ServiceException {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(testTag));
        Optional<Tag> result = tagService.getTagById(1L);
        assertTrue(result.isPresent());
        assertEquals(testTag, result.get());
    }

    @Test
    public void testGetTags() {
        int page = 0;
        int size = 10;
        String[] sortParams = {"name","asc"};
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        when(tagRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(testTag), pageable, 1));

        List<Tag> tags = tagService.getTags(page, size, sortParams);
        assertEquals(1, tags.size());
        assertEquals(testTag, tags.get(0));
    }

    @Test
    public void testGetMostWidelyUsedTagWithHighestCostByUserId() {
        Long userId = 1L;
        int page = 0;
        int size = 1;
        Pageable pageable = PageRequest.of(page, size);
        when(tagRepository.findMostWidelyUsedTagWithHighestCostByUserId(userId, pageable)).thenReturn(Collections.singletonList(testTag));

        Optional<Tag> result = tagService.getMostWidelyUsedTagWithHighestCostByUserId(userId, page, size);
        assertTrue(result.isPresent());
        assertEquals(testTag, result.get());
    }


    @Test
    public void testCreateTag() {
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
        Tag result = tagService.createTag(testTag);
        assertEquals(testTag, result);
    }

    @Test
    public void testCreateTagWhenTagExists() {
        when(tagRepository.getByName(testTag.getName())).thenReturn(Optional.of(testTag));
        Tag result = tagService.createTag(testTag);
        assertEquals(testTag, result);
        verify(tagRepository, times(0)).save(any(Tag.class));
    }



    @Test
    public void testDeleteOnlyOneTag() {
        testGiftCertificate.setTags(Collections.singletonList(testTag));
        when(giftCertificateRepository.getCertificatesByTagId(anyLong())).thenReturn(Collections.singletonList(testGiftCertificate));
        assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(1L));
        verify(tagRepository, times(0)).deleteById(1L);
    }

    @Test
    public void testDeleteTag() {
        testGiftCertificate.setTags(Arrays.asList(testTag, testTag2));
        when(giftCertificateRepository.getCertificatesByTagId(anyLong())).thenReturn(Collections.singletonList(testGiftCertificate));
        tagService.deleteTag(1L);
        verify(tagRepository, times(1)).deleteById(1L);
    }
}
