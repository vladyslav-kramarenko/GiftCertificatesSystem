package com.epam.esm.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
public record OrderRequest(
        @NotNull(message = "User ID should not be empty")
        @Positive(message = "User ID should be positive")
        Long userId,
        @NotNull(message = "Order should have at least 1 gift certificate")
        @Size(min = 1, message = "Order should have at least 1 gift certificate")
        List<GiftCertificateOrder> giftCertificates
) {
}
