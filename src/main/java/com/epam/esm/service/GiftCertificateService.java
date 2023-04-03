package com.epam.esm.service;

import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.GiftCertificate;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateService {
    Optional<GiftCertificate> getGiftCertificateById(Long id) throws ServiceException;

    GiftCertificate createGiftCertificate(GiftCertificate certificate) throws ServiceException;

    Optional<GiftCertificate> updateGiftCertificate(Long id, GiftCertificate certificate) throws ServiceException;

    boolean deleteGiftCertificate(Long id) throws ServiceException;

    List<GiftCertificate> getGiftCertificates() throws ServiceException;
}
