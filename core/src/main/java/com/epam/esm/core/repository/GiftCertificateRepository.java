package com.epam.esm.core.repository;

import com.epam.esm.core.entity.GiftCertificate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long>, CustomGiftCertificateRepository {
    List<GiftCertificate> findAll(Sort sort);

    Optional<GiftCertificate> getByName(@Param("name") String name);

    @Query("SELECT gc FROM GiftCertificate gc JOIN gc.tags t WHERE t.id = :tagId")
    List<GiftCertificate> getCertificatesByTagId(@Param("tagId") Long tagId);

    @Query(name = "search_gift_certificates_with_tags")
    Set<GiftCertificate> findAll(
            @Param("searchTerm") String searchTerm,
            @Param("sortConditions") String sortConditions,
            @Param("pageLimit") Integer pageLimit,
            @Param("pageOffset") Integer pageOffset,
            @Param("tagsFilter") String tagsFilter
    );

    @Query("SELECT gc FROM GiftCertificate gc JOIN FETCH gc.tags WHERE gc.id = :id")
    Optional<GiftCertificate> findByIdWithTags(@Param("id") Long id);

    List<GiftCertificate> findAll(Specification<GiftCertificate> specification, Pageable sortedByPriceDesc);
}