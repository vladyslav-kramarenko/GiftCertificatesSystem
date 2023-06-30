package com.epam.esm.core.repository.specs;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class GiftCertificateSpecification {
    public static Specification<GiftCertificate> hasNameLike(String searchTerm) {
        return (giftCertificate, cq, cb) -> cb.like(giftCertificate.get("name"), "%" + searchTerm + "%");
    }

    public static Specification<GiftCertificate> hasDescriptionLike(String searchTerm) {
        return (giftCertificate, cq, cb) -> cb.like(giftCertificate.get("description"), "%" + searchTerm + "%");
    }

    public static Specification<GiftCertificate> hasTagLike(String searchTerm) {
        return (giftCertificate, cq, cb) -> {
            Join<GiftCertificate, Tag> tags = giftCertificate.join("tags");
            return cb.like(tags.get("name"), "%" + searchTerm + "%");
        };
    }

    public static Specification<GiftCertificate> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (giftCertificate, cq, cb) -> cb.greaterThanOrEqualTo(giftCertificate.get("price"), minPrice);
    }

    public static Specification<GiftCertificate> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (giftCertificate, cq, cb) -> cb.lessThanOrEqualTo(giftCertificate.get("price"), maxPrice);
    }
}
