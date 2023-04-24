package com.epam.esm.core.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record GiftCertificateOrder(
        @NotNull(message = "Gift Certificate ID should not be empty")
        @Min(value = 0L, message = "Gift Certificate ID should be positive")
        Long giftCertificateId,
        @NotNull(message = "Quantity should not be empty")
        @Min(value = 1, message = "Quantity should be positive")
        Integer quantity) {
}

