package com.epam.esm.core.repository;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;

public interface CustomGiftCertificateRepository {
    void addTagToCertificate(GiftCertificate giftCertificate, Tag tag);
    void deleteAllTagsForCertificateById(Long id);
}

