package com.epam.esm.dao;

import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateDao {
    GiftCertificate create(GiftCertificate giftCertificate) throws DbException;

    Optional<GiftCertificate> getById(long id) throws DbException;

    List<GiftCertificate> getAll() throws DbException;

    GiftCertificate update(GiftCertificate giftCertificate) throws DbException;

    boolean delete(long id) throws DbException;
}