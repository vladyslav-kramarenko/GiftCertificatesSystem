package com.epam.esm.core.dto;

import java.math.BigDecimal;

public record MostUsedTagDTO(
        Long userId,
        Long tagId,
        String tagName,
        Long count,
        BigDecimal sum
) {
}
