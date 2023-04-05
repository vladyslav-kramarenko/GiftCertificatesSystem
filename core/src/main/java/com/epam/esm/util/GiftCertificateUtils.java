package com.epam.esm.util;

import com.epam.esm.model.GiftCertificate;

import java.math.BigDecimal;

import static com.epam.esm.util.CoreConstants.MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH;
import static com.epam.esm.util.CoreConstants.MAX_GIFT_CERTIFICATE_NAME_LENGTH;
import static com.epam.esm.util.TagUtils.validateTags;

/**
 * Utility class for GiftCertificate object validation and update.
 */
public class GiftCertificateUtils {
    /**
     * Validates the length of the name of the GiftCertificate.
     *
     * @param name name of the GiftCertificate.
     * @throws IllegalArgumentException if the name length is 0 or greater than MAX_GIFT_CERTIFICATE_NAME_LENGTH.
     */
    public static void validateGiftCertificateName(String name) throws IllegalArgumentException {
        if (name.length() == 0) {
            throw new IllegalArgumentException("Gift Certificate name cannot be empty");
        }
        if (name.length() > MAX_GIFT_CERTIFICATE_NAME_LENGTH) {
            throw new IllegalArgumentException("Gift Certificate name should be less than " +
                    MAX_GIFT_CERTIFICATE_NAME_LENGTH +
                    " symbols");
        }
    }

    /**
     * Validates the price of the GiftCertificate.
     *
     * @param price price of the GiftCertificate.
     * @throws IllegalArgumentException if the price is negative.
     */
    public static void validateGiftCertificatePrice(BigDecimal price) throws IllegalArgumentException {
        if (price.doubleValue() < 0) {
            throw new IllegalArgumentException("Gift Certificate price cannot be negative");
        }
    }

    /**
     * Validates the duration of the GiftCertificate.
     *
     * @param duration duration of the GiftCertificate.
     * @throws IllegalArgumentException if the duration is negative.
     */
    public static void validateGiftCertificateDuration(Integer duration) throws IllegalArgumentException {
        if (duration < 0) {
            throw new IllegalArgumentException("Gift Certificate duration cannot be negative");
        }
    }

    /**
     * Validates that the given parameter is not null.
     *
     * @param parameter     the parameter to validate.
     * @param parameterName the name of the parameter to use in the exception message.
     * @throws IllegalArgumentException if the parameter is null.
     */
    public static void validateForNull(Object parameter, String parameterName) throws IllegalArgumentException {
        if (parameter == null)
            throw new IllegalArgumentException("Gift Certificate " + parameterName + " cannot be empty");
    }

    /**
     * Validates the length of the description of the GiftCertificate.
     *
     * @param description description of the GiftCertificate.
     * @throws IllegalArgumentException if the description length is 0
     *                                  or greater than MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH.
     */
    public static void validateGiftCertificateDescription(String description) throws IllegalArgumentException {
        if (description.length() == 0) {
            throw new IllegalArgumentException("Gift Certificate description cannot be empty");
        }
        if (description.length() > MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Gift Certificate description should be less than " +
                            MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH +
                            " symbols");
        }
    }

    /**
     * Updates a given GiftCertificate with the new data from another GiftCertificate.
     * Only fields that are not null in the new GiftCertificate are updated in the given GiftCertificate.
     *
     * @param giftCertificateToUpdate    the GiftCertificate to update.
     * @param giftCertificateWithNewData the GiftCertificate containing the new data.
     */
    public static void updateCertificate(
            GiftCertificate giftCertificateToUpdate,
            GiftCertificate giftCertificateWithNewData) {
        if (giftCertificateWithNewData.getDescription() != null) {
            validateGiftCertificateDescription(giftCertificateWithNewData.getDescription());
            giftCertificateToUpdate.setDescription(giftCertificateWithNewData.getDescription());
        }
        if (giftCertificateWithNewData.getName() != null) {
            validateGiftCertificateName(giftCertificateWithNewData.getName());
            giftCertificateToUpdate.setName(giftCertificateWithNewData.getName());
        }
        if (giftCertificateWithNewData.getDuration() != null) {
            validateGiftCertificateDuration(giftCertificateWithNewData.getDuration());
            giftCertificateToUpdate.setDuration(giftCertificateWithNewData.getDuration());
        }
        if (giftCertificateWithNewData.getPrice() != null) {
            validateGiftCertificatePrice(giftCertificateWithNewData.getPrice());
            giftCertificateToUpdate.setPrice(giftCertificateWithNewData.getPrice());
        }
        if (giftCertificateWithNewData.getTags() != null) {
            validateTags(giftCertificateWithNewData.getTags());
            giftCertificateToUpdate.setTags(giftCertificateWithNewData.getTags());
        }
    }
}
