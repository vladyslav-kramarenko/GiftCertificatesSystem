package com.epam.esm.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NestedOrderDTO extends RepresentationModel<NestedOrderDTO> {

    private Long id;
    private BigDecimal sum;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private List<NestedGiftCertificateDTO> giftCertificates;
}