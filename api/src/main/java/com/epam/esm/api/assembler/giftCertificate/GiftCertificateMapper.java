package com.epam.esm.api.assembler.giftCertificate;

import com.epam.esm.api.dto.giftCertificate.BaseGiftCertificateDTO;
import com.epam.esm.core.entity.GiftCertificate;

public class GiftCertificateMapper {
    public static <T extends BaseGiftCertificateDTO> T mapGiftCertificateToDto(GiftCertificate giftCertificate, T dto) {
        dto.setId(giftCertificate.getId());
        dto.setName(giftCertificate.getName());
        dto.setDescription(giftCertificate.getDescription());
        dto.setPrice(giftCertificate.getPrice());
        dto.setDuration(giftCertificate.getDuration());
        dto.setCreateDate(giftCertificate.getCreateDate());
        dto.setLastUpdateDate(giftCertificate.getLastUpdateDate());
        dto.setImg(giftCertificate.getImg());
        return dto;
    }
}
