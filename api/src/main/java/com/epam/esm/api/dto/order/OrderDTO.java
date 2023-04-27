package com.epam.esm.api.dto.order;

import com.epam.esm.api.dto.NestedUserDTO;
import com.epam.esm.api.dto.giftCertificate.OrderGiftCertificateDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTO extends BaseOrderDTO {
    private NestedUserDTO user;
    private List<OrderGiftCertificateDTO> orderGiftCertificateDTOS;
}