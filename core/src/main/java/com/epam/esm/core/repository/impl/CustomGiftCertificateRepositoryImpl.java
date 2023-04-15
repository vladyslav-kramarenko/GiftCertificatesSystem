package com.epam.esm.core.repository.impl;

import jakarta.persistence.EntityManager;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.repository.CustomGiftCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomGiftCertificateRepositoryImpl implements CustomGiftCertificateRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void addTagToCertificate(GiftCertificate giftCertificate, Tag tag) {
        if (!hasCertificateTag(giftCertificate, tag)) {
            entityManager.createNativeQuery("INSERT INTO gift_certificate_has_tag (gift_certificate_id, tag_id) VALUES (?, ?)")
                    .setParameter(1, giftCertificate.getId())
                    .setParameter(2, tag.getId())
                    .executeUpdate();
        } else {
            throw new IllegalArgumentException("Certificate with id " + giftCertificate.getId() + " already has the tag: " + tag);
        }
    }

    public void deleteAllTagsForCertificateById(Long giftCertificateId) {
        entityManager.createNativeQuery(
                        "DELETE FROM gift_certificate_has_tag WHERE gift_certificate_id = ?")
                .setParameter(1, giftCertificateId)
                .executeUpdate();
    }

    private boolean hasCertificateTag(GiftCertificate giftCertificate, Tag tag) {
        int count = ((Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM gift_certificate_has_tag WHERE gift_certificate_id = ? AND tag_id = ?")
                .setParameter(1, giftCertificate.getId())
                .setParameter(2, tag.getId())
                .getSingleResult()).intValue();
        return count > 0;
    }
}
