package com.epam.esm.api.dto.order;

import com.epam.esm.api.dto.giftCertificate.BaseGiftCertificateDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NestedOrderDTO extends BaseOrderDTO {
    private List<BaseGiftCertificateDTO> giftCertificates;
}