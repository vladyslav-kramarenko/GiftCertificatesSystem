package com.epam.esm.controller;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.impl.GiftCertificateServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.epam.esm.util.testUtils.generateGiftCertificateWithoutId;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class GiftCertificateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private GiftCertificateService giftCertificateService
            = new GiftCertificateServiceImpl(Mockito.mock(GiftCertificateDao.class));

    @Test
    public void testGetGiftCertificateById() throws Exception {
        GiftCertificate certificate = generateGiftCertificateWithoutId();
        long id = 2L;
        certificate.setId(id);
        certificate.setCreateDate(LocalDateTime.now().minusDays(1));
        certificate.setLastUpdateDate(LocalDateTime.now());

        given(giftCertificateService.getGiftCertificateById(id)).willReturn(Optional.of(certificate));
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new GiftCertificateController((GiftCertificateServiceImpl) giftCertificateService)
                )
                .build();
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                        "/certificates/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(certificate.getName()))
                .andExpect(jsonPath("$.description").value(certificate.getDescription()))
                .andExpect(jsonPath("$.price").value(certificate.getPrice()))
                .andExpect(jsonPath("$.duration").value(certificate.getDuration()))
                .andExpect(jsonPath("$.tags[0].id").value(certificate.getTags().get(0).getId()))
                .andExpect(jsonPath("$.tags[0].name").value(certificate.getTags().get(0).getName()))
                .andExpect(jsonPath("$.tags[1].id").value(certificate.getTags().get(1).getId()))
                .andExpect(jsonPath("$.tags[1].name").value(certificate.getTags().get(1).getName()))
                .andExpect(jsonPath("$.createDate").value(certificate.getCreateDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.lastUpdateDate").value(certificate.getLastUpdateDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}
