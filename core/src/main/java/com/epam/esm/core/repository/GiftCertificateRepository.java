package com.epam.esm.core.repository;

import com.epam.esm.core.entity.GiftCertificate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long>, CustomGiftCertificateRepository {

    List<GiftCertificate> findAll(Sort sort);

    @Query("SELECT gc FROM GiftCertificate gc JOIN gc.tags t WHERE t.id = :tagId")
    List<GiftCertificate> getCertificatesByTagId(Long tagId);

    @Procedure(name = "search_gift_certificates_inout")
    List<GiftCertificate> findAll(
            @Param("searchTerm") String searchTerm,
            @Param("sortConditions") String sortConditions,
            @Param("pageOffset") Integer pageOffset,
            @Param("pageLimit") Integer pageLimit
    );
}