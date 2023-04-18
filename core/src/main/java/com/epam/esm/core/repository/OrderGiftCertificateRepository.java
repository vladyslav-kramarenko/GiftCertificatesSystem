package com.epam.esm.core.repository;

import com.epam.esm.core.entity.OrderGiftCertificate;
import com.epam.esm.core.entity.OrderGiftCertificateId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderGiftCertificateRepository extends JpaRepository<OrderGiftCertificate, OrderGiftCertificateId> {
}