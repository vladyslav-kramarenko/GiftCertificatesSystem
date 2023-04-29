package com.epam.esm.core.util;

import com.epam.esm.core.entity.GiftCertificate;

/**
 * Utility class for GiftCertificate object validation and update.
 */
public class GiftCertificateUtils {
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
            giftCertificateToUpdate.setDescription(giftCertificateWithNewData.getDescription());
        }
        if (giftCertificateWithNewData.getName() != null) {
            giftCertificateToUpdate.setName(giftCertificateWithNewData.getName());
        }
        if (giftCertificateWithNewData.getDuration() != null) {
            giftCertificateToUpdate.setDuration(giftCertificateWithNewData.getDuration());
        }
        if (giftCertificateWithNewData.getPrice() != null) {
            giftCertificateToUpdate.setPrice(giftCertificateWithNewData.getPrice());
        }
    }
}
