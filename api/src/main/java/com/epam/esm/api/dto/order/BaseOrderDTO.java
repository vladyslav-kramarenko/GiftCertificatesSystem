package com.epam.esm.api.dto.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BaseOrderDTO extends RepresentationModel<BaseOrderDTO> {
    private Long id;
    private BigDecimal sum;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
}