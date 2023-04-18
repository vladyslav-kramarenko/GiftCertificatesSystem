package com.epam.esm.core.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
        @NotNull(message = "Gift Certificate ID should not be empty")
        @Positive(message = "Gift Certificate ID should be positive")
        Long giftCertificateID,
        @NotNull(message = "Quantity should not be empty")
        @Positive(message = "Quantity should be positive")
        Integer quantity
) {}
