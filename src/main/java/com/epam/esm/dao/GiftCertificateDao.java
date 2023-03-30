package com.epam.esm.dao;

import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GiftCertificateDao {
    GiftCertificate create(GiftCertificate giftCertificate) throws DbException;

    Optional<GiftCertificate> getById(long id) throws SQLException;

    List<GiftCertificate> getAll() throws SQLException;

    GiftCertificate update(GiftCertificate giftCertificate) throws SQLException;

    void delete(long id);
}