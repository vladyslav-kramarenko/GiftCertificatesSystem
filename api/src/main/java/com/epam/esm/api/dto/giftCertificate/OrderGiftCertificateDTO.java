package com.epam.esm.api.dto.giftCertificate;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderGiftCertificateDTO extends BaseGiftCertificateDTO {
    private int count;
    private BigDecimal sum;
}
