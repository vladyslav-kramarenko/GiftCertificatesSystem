package com.epam.esm.core.service;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.impl.GiftCertificateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class GiftCertificateServiceImplTest {

    @Mock
    private GiftCertificateRepository giftCertificateRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private GiftCertificateServiceImpl giftCertificateService;

    private GiftCertificate giftCertificate;

    @BeforeEach
    public void setUp() {
        giftCertificate = new GiftCertificate();
        giftCertificate.setId(1L);
        giftCertificate.setName("Test Certificate");
        giftCertificate.setDescription("Test Description");
        giftCertificate.setPrice(BigDecimal.valueOf(100));
        giftCertificate.setDuration(30);
    }

    @Test
    public void getGiftCertificateById_success() {
        when(giftCertificateRepository.findByIdWithTags(anyLong())).thenReturn(Optional.of(giftCertificate));
        Optional<GiftCertificate> foundGiftCertificate = giftCertificateService.getGiftCertificateById(1L);
        assertEquals(giftCertificate, foundGiftCertificate.orElse(null));
    }

    @Test
    public void createGiftCertificate_success() throws ServiceException {
        when(giftCertificateRepository.save(any(GiftCertificate.class))).thenReturn(giftCertificate);
        GiftCertificate createdGiftCertificate = giftCertificateService.createGiftCertificate(giftCertificate);
        assertEquals(giftCertificate, createdGiftCertificate);
    }

    @Test
    public void deleteGiftCertificate_success() {
        giftCertificateService.deleteGiftCertificate(1L);
        verify(giftCertificateRepository).deleteById(anyLong());
    }

    @Test
    public void getGiftCertificates_success() {
        when(giftCertificateRepository.findAll(
                any(String.class), any(String.class), any(Integer.class), any(Integer.class), any(String.class)))
                .thenReturn((Arrays.stream(new GiftCertificate[]{giftCertificate}).collect(Collectors.toSet())));

        int page = 0;
        int size = 10;
        String[] sortParams = {"id", "asc"};
        List<GiftCertificate> giftCertificates = giftCertificateService.getGiftCertificates(null, null, page, size, sortParams);

        assertEquals(1, giftCertificates.size());
        assertEquals(giftCertificate, giftCertificates.get(0));
    }

    @Test
    public void updateGiftCertificate_withoutTags_success() throws ServiceException {
        GiftCertificate updatedGiftCertificate = new GiftCertificate();
        updateMainGiftCertificateParameters(updatedGiftCertificate);

        when(giftCertificateRepository.findById(anyLong())).thenReturn(Optional.of(giftCertificate));
        when(giftCertificateRepository.save(any(GiftCertificate.class))).thenReturn(updatedGiftCertificate);

        Optional<GiftCertificate> result = giftCertificateService.updateGiftCertificate(1L, updatedGiftCertificate);

        assertTrue(result.isPresent());
        assertEquals(updatedGiftCertificate.getName(), result.get().getName());
        assertEquals(updatedGiftCertificate.getDescription(), result.get().getDescription());
        assertEquals(updatedGiftCertificate.getPrice(), result.get().getPrice());
        assertEquals(updatedGiftCertificate.getDuration(), result.get().getDuration());
    }

    @Test
    public void updateGiftCertificate_notFound() throws ServiceException {
        when(giftCertificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<GiftCertificate> result = giftCertificateService.updateGiftCertificate(1L, giftCertificate);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void updateGiftCertificate_withTags() throws ServiceException {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");
        giftCertificate.setTags(Collections.singletonList(tag));

        GiftCertificate updatedGiftCertificate = new GiftCertificate();
        updateMainGiftCertificateParameters(updatedGiftCertificate);
        updatedGiftCertificate.setTags(Collections.singletonList(tag));

        when(giftCertificateRepository.findById(anyLong())).thenReturn(Optional.of(giftCertificate));
        when(giftCertificateRepository.save(any(GiftCertificate.class))).thenReturn(updatedGiftCertificate);
        when(tagRepository.getByName(any(String.class))).thenReturn(Optional.of(tag));
        doNothing().when(giftCertificateRepository).deleteAllTagsForCertificateById(anyLong());

        Optional<GiftCertificate> result = giftCertificateService.updateGiftCertificate(1L, updatedGiftCertificate);
        assertTrue(result.isPresent());
        assertEquals(updatedGiftCertificate.getName(), result.get().getName());
        assertEquals(updatedGiftCertificate.getDescription(), result.get().getDescription());
        assertEquals(updatedGiftCertificate.getPrice(), result.get().getPrice());
        assertEquals(updatedGiftCertificate.getDuration(), result.get().getDuration());
    }

    @Test
    public void updateGiftCertificate_createNewTag() throws ServiceException {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");
        giftCertificate.setTags(Collections.singletonList(tag));

        GiftCertificate updatedGiftCertificate = new GiftCertificate();
        updateMainGiftCertificateParameters(updatedGiftCertificate);
        updatedGiftCertificate.setTags(Collections.singletonList(tag));

        when(giftCertificateRepository.findById(anyLong())).thenReturn(Optional.of(giftCertificate));
        when(giftCertificateRepository.save(any(GiftCertificate.class))).thenReturn(updatedGiftCertificate);
        when(tagRepository.getByName(any(String.class))).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        doNothing().when(giftCertificateRepository).deleteAllTagsForCertificateById(anyLong());

        Optional<GiftCertificate> result = giftCertificateService.updateGiftCertificate(1L, updatedGiftCertificate);

        assertTrue(result.isPresent());
        assertEquals(updatedGiftCertificate.getName(), result.get().getName());
        assertEquals(updatedGiftCertificate.getDescription(), result.get().getDescription());
        assertEquals(updatedGiftCertificate.getPrice(), result.get().getPrice());
        assertEquals(updatedGiftCertificate.getDuration(), result.get().getDuration());
    }

    private void updateMainGiftCertificateParameters(GiftCertificate updatedGiftCertificate) {
        updatedGiftCertificate.setId(1L);
        updatedGiftCertificate.setName("Updated Test Certificate");
        updatedGiftCertificate.setDescription("Updated Test Description");
        updatedGiftCertificate.setPrice(BigDecimal.valueOf(200));
        updatedGiftCertificate.setDuration(60);
    }
}
