package com.epam.esm.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GiftCertificateOrder(
        @NotNull(message = "Gift Certificate ID should not be empty")
        @Positive(message = "Gift Certificate ID should be positive")
        Long giftCertificateId,
        @NotNull(message = "Quantity should not be empty")
        @Positive(message = "Quantity should be positive")
        Integer quantity) {
}

