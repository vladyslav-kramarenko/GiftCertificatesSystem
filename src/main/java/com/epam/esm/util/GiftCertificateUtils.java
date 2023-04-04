package com.epam.esm.util;

import com.epam.esm.model.GiftCertificate;

import java.math.BigDecimal;

import static com.epam.esm.util.Constants.MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH;
import static com.epam.esm.util.Constants.MAX_GIFT_CERTIFICATE_NAME_LENGTH;

public class GiftCertificateUtils {

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

    public static void validateGiftCertificatePrice(BigDecimal price) throws IllegalArgumentException {
        if (price.doubleValue() < 0) {
            throw new IllegalArgumentException("Gift Certificate price cannot be negative");
        }
    }

    public static void validateGiftCertificateDuration(Integer duration) throws IllegalArgumentException {
        if (duration < 0) {
            throw new IllegalArgumentException("Gift Certificate duration cannot be negative");
        }
    }


    public static void validateForNull(Object parameter, String parameterName) throws IllegalArgumentException {
        if (parameter == null)
            throw new IllegalArgumentException("Gift Certificate " + parameterName + " cannot be empty");
    }

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

    public static void updateCertificate(GiftCertificate giftCertificateToUpdate, GiftCertificate giftCertificateWithNewData) {
        if (giftCertificateWithNewData.getDescription() != null) {
            validateGiftCertificateDescription(giftCertificateWithNewData.getDescription());
            giftCertificateToUpdate.setDescription(giftCertificateWithNewData.getDescription());
        }
        if (giftCertificateWithNewData.getName() != null) {
            validateGiftCertificateName(giftCertificateWithNewData.getName());
            giftCertificateToUpdate.setName(giftCertificateWithNewData.getDescription());
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
            giftCertificateToUpdate.setTags(giftCertificateWithNewData.getTags());
        }
    }
}
