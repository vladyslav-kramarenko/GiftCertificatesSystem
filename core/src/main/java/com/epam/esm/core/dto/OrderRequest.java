package com.epam.esm.core.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record OrderRequest(
        @NotNull(message = "User ID should not be empty")
        @Min(value = 0L, message = "User ID should be positive")
        Long userId,
        @NotNull(message = "Order should have at least 1 gift certificate")
        @Size(min = 1, message = "Order should have at least 1 gift certificate")
        @Valid
        List<GiftCertificateOrder> giftCertificates
) {
}
