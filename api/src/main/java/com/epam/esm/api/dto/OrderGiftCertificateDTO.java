package com.epam.esm.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderGiftCertificateDTO extends RepresentationModel<OrderGiftCertificateDTO> {
    private int count;
    private BigDecimal sum;
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
}
